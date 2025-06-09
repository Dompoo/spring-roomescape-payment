package roomescape.test_util;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.repository.impl.*;

import java.time.LocalDate;
import java.time.LocalTime;

@DataJpaTest
@Import({DataInserter.class, DataCleaner.class,
        MemberRepositoryImpl.class,
        PaymentRepositoryImpl.class,
        ReservationItemRepositoryImpl.class,
        ReservationRepositoryImpl.class,
        ReservationThemeRepositoryImpl.class,
        ReservationTimeRepositoryImpl.class,
})
@ActiveProfiles("test")
public abstract class RepositoryTest {

    @Autowired
    private DataInserter dataInserter;
    @Autowired
    private DataCleaner dataCleaner;

    @AfterEach
    void tearDown() {
        dataCleaner.clean();
    }

    public Payment insertPayment(String paymentKey, int amount, Reservation reservation) {
        return dataInserter.insertPayment(paymentKey, amount, reservation);
    }

    public Reservation insertReservation(Member member, ReservationItem reservationItem, ReservationStatus reservationStatus) {
        return dataInserter.insertReservation(member, reservationItem, reservationStatus);
    }

    public Member insertMember(String email, String password, String name, MemberRole role) {
        return dataInserter.insertMember(email, password, name, role);
    }

    public ReservationTime insertReservationTime(LocalTime startAt) {
        return dataInserter.insertReservationTime(startAt);
    }

    public ReservationTheme insertReservationTheme(String name, String description, String thumbnail) {
        return dataInserter.insertReservationTheme(name, description, thumbnail);
    }

    public ReservationItem insertReservationItem(LocalDate date, ReservationTime time, ReservationTheme theme) {
        return dataInserter.insertReservationItem(date, time, theme);
    }
}

