package roomescape.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.global.exception.business.ExternalApiException;
import roomescape.global.logging.util.Log;

import java.util.NoSuchElementException;
import java.util.UUID;

import static roomescape.dto.response.TossPaymentResponse.PaymentStatus;
import static roomescape.global.exception.business.BusinessErrorCode.*;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentApproveClient paymentApproveClient;
    private final PaymentCheckClient paymentCheckClient;
    private final PaymentCancelClient paymentCancelClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void approveAndSave(String paymentKey, String orderId, int amount, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));
        paymentRepository.save(new Payment(paymentKey, amount, reservation));
        approvePaymentSafely(paymentKey, orderId, amount);
    }

    private void approvePaymentSafely(String paymentKey, String orderId, int amount) {
        try {
            Log.business("결제 승인", "결제 승인 API 호출", "paymentKey : %s".formatted(paymentKey), getClass());
            paymentApproveClient.approve(paymentKey, orderId, amount);
            Log.business("결제 승인", "결제 승인 API 호출 성공", "paymentKey : %s".formatted(paymentKey), getClass());
        } catch (RestClientException e) {
            Log.business("결제 승인", "결제 승인 API 호출 실패", "paymentKey : %s".formatted(paymentKey), getClass());
            // 결제 승인 요청 중 실패 -> 결제 승인 여부를 모르므로 결제 확인 요청
            int attempts = 0;
            while (attempts < 3) {
                try {
                    attempts++;
                    Log.business("결제 승인", "복구하기 위해 결제 확인 API 호출", "attempt : %s, paymentKey : %s".formatted(attempts, paymentKey), getClass());
                    if (paymentCheckClient.checkStatus(paymentKey) == PaymentStatus.DONE) {
                        Log.business("결제 승인", "결제 확인 API로 복구 성공", "paymentKey : %s".formatted(paymentKey), getClass());
                        return;
                    }
                } catch (HttpServerErrorException | ResourceAccessException ignored) {
                }
            }

            Log.business("결제 승인", "결제 확인 API로 복구 실패", "paymentKey : %s".formatted(paymentKey), getClass());
            // 결제 확인 요청도 실패 -> 안전하게 결제 취소 (멱등성 보장)
            // 토스 API 멱등성 관련 문서 : https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%B7%A8%EC%86%8C
            String idempotencyKey = UUID.randomUUID().toString();
            boolean cancelSuccess = false;
            attempts = 0;
            while (attempts < 3) {
                try {
                    attempts++;
                    Log.business("결제 승인", "복구하기 위해 결제 취소 API 호출", "attempt : %s, paymentKey : %s".formatted(attempts, paymentKey), getClass());
                    paymentCancelClient.cancelIdempotently(paymentKey, idempotencyKey);
                    cancelSuccess = true;
                } catch (HttpServerErrorException | ResourceAccessException ignored) {
                }
            }
            if (cancelSuccess) {
                Log.business("결제 승인", "결제 취소 API로 복구 성공", "paymentKey : %s".formatted(paymentKey), getClass());
                throw new ExternalApiException(PAYMENT_CANCELED);
            } else {
                Log.business("결제 승인", "결제 취소 API로 복구 실패, 결제 취소 대기열에 추가", "paymentKey : %s, idempotencyKey : %s".formatted(paymentKey, idempotencyKey), getClass());
                // 결제 취소 시도도 실패했다면, 일단 저장해놓고 추후에 취소 (5분 주기)
                paymentCancelClient.addToCancelSchedule(paymentKey, idempotencyKey);
                throw new ExternalApiException(PAYMENT_CANCEL_SCHEDULED);
            }
        } catch (Exception e) {
            Log.business("결제 승인", "예상치 못한 예외 발생", "paymentKey : %s".formatted(paymentKey), getClass());
            // 예상하지 못한 예외 -> 관리자 문의
            // TODO : 개발자 수준에서 더 처리할 수 있는 것이 있다면 추가하기
            throw new ExternalApiException(PAYMENT_FAILED_UNKNOWN);
        }
    }
}
