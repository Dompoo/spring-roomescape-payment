package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingReservationResponse;
import roomescape.service.member.MemberService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationItemService reservationItemService;
    private final MemberService memberService;
    private final ReservationThemeService reservationThemeService;
    private final ReservationTimeService reservationTimeService;

    @Transactional
    public ReservationResponse addReservation(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.ACCEPTED, false);
    }

    @Transactional
    public ReservationResponse addPendingReservation(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.PENDING, true);
    }

    private ReservationResponse createReservation(
            final CreateReservationRequest request,
            final ReservationStatus status,
            final boolean requiresExistingReservation) {

        final Member member = memberService.getMemberById(request.memberId());
        final ReservationTime time = reservationTimeService.getReservationTimeById(request.timeId());
        final ReservationTheme theme = reservationThemeService.getThemeById(request.themeId());
        final LocalDate date = request.date();

        validateReservationAvailability(date, time, theme, requiresExistingReservation);

        final ReservationItem reservationItem = reservationItemService.createReservationItemIfNotExist(
                date, time, theme);

        validateDuplicateReservation(member, reservationItem);

        final Reservation saved = reservationRepository.save(
                Reservation.builder()
                        .member(member)
                        .reservationItem(reservationItem)
                        .reservationStatus(status)
                        .build()
        );
        return ReservationResponse.from(saved);
    }

    private void validateReservationAvailability(
            final LocalDate date,
            final ReservationTime time,
            final ReservationTheme theme,
            final boolean requiresExistingReservation) {

        final boolean reservationExists = reservationItemService.isExistReservationItem(date, time, theme);

        if (requiresExistingReservation && !reservationExists) {
            throw new IllegalArgumentException("[ERROR] 대기 예약은 기존 예약이 있을 때만 가능합니다.");
        }

        if (!requiresExistingReservation && reservationExists) {
            throw new IllegalArgumentException("[ERROR] 이미 예약된 시간입니다.");
        }
    }

    private void validateDuplicateReservation(final Member member, final ReservationItem reservationItem) {
        if (reservationRepository.existsByMemberAndReservationItem(member, reservationItem)) {
            throw new IllegalArgumentException("[ERROR] 이미 예약을 등록하였습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getFilteredReservations(final Long memberId,
                                                             final Long themeId,
                                                             final LocalDate dateFrom,
                                                             final LocalDate dateTo) {
        final List<Reservation> reservations = reservationRepository.findByMemberIdAndThemeIdAndDateFromAndDateTo(
                memberId,
                themeId,
                dateFrom,
                dateTo
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingReservationResponse> getAllWaitingReservations() {
        List<Reservation> waitingReservations = reservationRepository.findByReservationStatusOrderByIdDesc(ReservationStatus.PENDING);
        return waitingReservations.stream()
                .map(WaitingReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyPageReservationResponse> getReservationsByMemberId(Long memberId) {
        final Member member = memberService.getMemberById(memberId);
        List<Reservation> myReservations = reservationRepository.findByMemberId(member.getId());
        return myReservations.stream()
                .map(reservation -> {
                            final int priority = calculatePriority(reservation);
                            return MyPageReservationResponse.from(reservation, priority);
                        }
                )
                .toList();
    }

    private int calculatePriority(Reservation reservation) {
        Long reservationItemId = reservation.getReservationItem().getId();
        Long currentReservationId = reservation.getId();

        return (int) reservationRepository.countByReservationItemIdAndIdLessThan(
                reservationItemId, currentReservationId
        );
    }

    @Transactional
    public void denyPendingReservation(Long reservationId) {
        Reservation waitingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));

        if (waitingReservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("[ERROR] 대기 상태의 예약만 거절할 수 있습니다.");
        }

        waitingReservation.changeStatusToDenied();
    }

    @Transactional
    public void removeReservation(Long reservationId) {
        Reservation targetReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));

        if (targetReservation.getReservationStatus() == ReservationStatus.PENDING) {
            deleteReservationOnly(targetReservation);
        } else if (targetReservation.getReservationStatus() == ReservationStatus.ACCEPTED) {
            handleAcceptedReservationRemoval(targetReservation);
        }
    }

    private void handleAcceptedReservationRemoval(Reservation targetReservation) {
        ReservationItem reservationItem = targetReservation.getReservationItem();

        reservationRepository.findFirstByReservationItemAndReservationStatusOrderByIdAsc(
                reservationItem, ReservationStatus.PENDING
        ).ifPresentOrElse(
                nextReservation -> {
                    nextReservation.changeStatusToAccepted();
                    reservationRepository.save(nextReservation);
                    deleteReservationOnly(targetReservation);
                },
                () -> deleteReservationWithItem(targetReservation, reservationItem)
        );
    }

    private void deleteReservationOnly(Reservation reservation) {
        reservationRepository.deleteById(reservation.getId());
    }

    private void deleteReservationWithItem(Reservation reservation, ReservationItem reservationItem) {
        reservationRepository.deleteById(reservation.getId());
        reservationItemService.deleteReservationItem(reservationItem);
    }
}
