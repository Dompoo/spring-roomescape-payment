package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.global.exception.business.impl.NotFoundException;

import static roomescape.global.exception.business.BusinessErrorCode.PAYMENT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class PaymentHelper {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Payment getByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException(PAYMENT_NOT_EXIST));
    }

    @Transactional
    public void deleteByReservationIdIfExist(Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }
}
