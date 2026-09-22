/* package Pensionaten.service;

import Pensionaten.dto.BookingDTO;
import Pensionaten.models.Customer;
import Pensionaten.models.Room;
import Pensionaten.models.RoomType;
import Pensionaten.repositories.BookingRepository;
import Pensionaten.repositories.CustomerRepository;
import Pensionaten.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class BookingServiceTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Customer customer;
    private Room room;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .firstName("Namn").lastName("Namnet")
                .email("namn@test123.com").phone("111111111")
                .build();

        customer = customerRepository.save(customer);

        room = Room.builder()
                .roomNumber("600").roomType(RoomType.DOUBLE)
                .extraBeds(1).pricePerNight(1500)
                .build();

        room = roomRepository.save(room);
    }

    @Test
    void findAll_shouldReturnBookings() {

        BookingDTO dto = createValidBookingDTO();

        bookingService.saveBooking(dto);

        assertEquals(1, bookingService.findAll().size());
    }

    @Test
    void findById_shouldReturnBooking() {

        BookingDTO dto = createValidBookingDTO();

        bookingService.saveBooking(dto);

        Long bookingId = bookingRepository.findAll().get(0).getId();

        BookingDTO result = bookingService.findById(bookingId);

        assertEquals(customer.getId(), result.getCustomerId());
    }

    @Test
    void saveBooking_shouldSaveValidBooking() {

        BookingDTO dto = createValidBookingDTO();

        boolean result = bookingService.saveBooking(dto);

        assertTrue(result);
        assertEquals(1, bookingRepository.count());
    }

    @Test
    void saveBooking_shouldFailWhenCustomerMissing() {

        BookingDTO dto = createValidBookingDTO();
        dto.setCustomerId(null);

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
    }

    @Test
    void saveBooking_shouldFailWhenRoomMissing() {

        BookingDTO dto = createValidBookingDTO();
        dto.setRoomId(null);

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
    }

    @Test
    void saveBooking_shouldFailWhenDatesInvalid() {

        BookingDTO dto = createValidBookingDTO();

        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(2));

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
    }

    @Test
    void saveBooking_shouldFailWhenCustomerHasConflict() {

        BookingDTO firstBooking = createValidBookingDTO();

        bookingService.saveBooking(firstBooking);

        BookingDTO secondBooking = createValidBookingDTO();

        boolean result = bookingService.saveBooking(secondBooking);

        assertFalse(result);
    }

    @Test
    void saveBooking_shouldFailWhenRoomIsOccupied() {

        BookingDTO firstBooking = createValidBookingDTO();

        bookingService.saveBooking(firstBooking);

        Customer secondCustomer = Customer.builder()
                .firstName("Boris").lastName("Ivis")
                .email("yo123@test.se").phone("123456789")
                .build();

        secondCustomer = customerRepository.save(secondCustomer);

        BookingDTO secondBooking = createValidBookingDTO();
        secondBooking.setCustomerId(secondCustomer.getId());

        boolean result = bookingService.saveBooking(secondBooking);

        assertFalse(result);
    }

    @Test
    void deleteById_shouldDeleteBooking() {

        BookingDTO dto = createValidBookingDTO();

        bookingService.saveBooking(dto);

        Long bookingId = bookingRepository.findAll().get(0).getId();

        boolean result = bookingService.deleteById(bookingId);

        assertTrue(result);
        assertEquals(0, bookingRepository.count());
    }

    @Test
    void deleteById_shouldReturnFalseWhenBookingMissing() {

        boolean result = bookingService.deleteById(999L);

        assertFalse(result);
    }

    private BookingDTO createValidBookingDTO() {

        BookingDTO dto = new BookingDTO();

        dto.setCustomerId(customer.getId());
        dto.setRoomId(room.getId());

        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));

        dto.setNumberOfGuests(2);

        return dto;
    }
}
*/
