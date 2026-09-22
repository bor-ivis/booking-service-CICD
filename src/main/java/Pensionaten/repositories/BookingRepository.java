package Pensionaten.repositories;

import Pensionaten.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

// Repository för bokningar. Spring Data JPA skapar grundläggande databasmetoder automatiskt
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Används för att kontrollera om en kund har bokningar innan kunden tas bort
    boolean existsByCustomerId(Long customerId);

    // Används för att kontrollera om ett rum har bokningar innan rummet tas bort
    boolean existsByRoomId(Long roomId);

    // Kontrollerar om kunden redan har en bokning som överlappar med de valda datumen
    @Query("""
        SELECT COUNT (b) > 0
        FROM Booking b
        WHERE b.customerId = :customerId
        AND b.checkInDate < :checkOutDate
        AND b.checkOutDate > :checkInDate
        AND ( :bookingId IS NULL OR b.id <> :bookingId)
    """)

    boolean existsCustomerBookingConflict(
            @Param("customerId") Long customerId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("bookingId") Long bookingId
    );
}