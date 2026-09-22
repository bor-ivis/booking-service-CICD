package Pensionaten.service;

import Pensionaten.dto.BookingDTO;
import Pensionaten.dto.CustomerDTO;
import Pensionaten.models.Booking;
import Pensionaten.models.Room;
import Pensionaten.models.RoomType;
import Pensionaten.repositories.BookingRepository;
import Pensionaten.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private Booking booking;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {

        room = Room.builder()
                .id(9L)
                .roomNumber("101")
                .roomType(RoomType.SINGLE)
                .extraBeds(0)
                .pricePerNight(1000)
                .build();

        booking = new Booking();
        booking.setId(9L);
        booking.setCustomerId(9L);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2026, 6, 1));
        booking.setCheckOutDate(LocalDate.of(2026, 6, 3));
        booking.setNumberOfGuests(1);

        customerDTO = new CustomerDTO();
        customerDTO.setId(9L);
        customerDTO.setFirstName("Namn");
        customerDTO.setLastName("Efternamn");
    }

    @Test
    void findAll_shouldReturnBookingDTOList() {

        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(restTemplate.getForObject(anyString(), eq(CustomerDTO.class))).thenReturn(customerDTO);

        List<BookingDTO> result = bookingService.findAll();

        assertEquals(1, result.size());
        assertEquals("Namn", result.get(0).getCustomerFirstName());
        assertEquals("101", result.get(0).getRoomNumber());
    }

    @Test
    void findById_shouldReturnBookingDTO_whenBookingExists() {

        when(bookingRepository.findById(9L)).thenReturn(Optional.of(booking));
        when(restTemplate.getForObject(anyString(), eq(CustomerDTO.class))).thenReturn(customerDTO);

        BookingDTO result = bookingService.findById(9L);

        assertNotNull(result);
        assertEquals(9L, result.getId());
        assertEquals("Namn", result.getCustomerFirstName());
    }

    @Test
    void findById_shouldReturnNull_whenBookingDoesNotExist() {

        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        BookingDTO result = bookingService.findById(99L);

        assertNull(result);
    }

    @Test
    void saveBooking_shouldReturnFalse_whenDatesAreMissing() {

        BookingDTO dto = new BookingDTO();

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_shouldReturnFalse_whenCheckoutIsBeforeCheckin() {

        BookingDTO dto = new BookingDTO();
        dto.setCustomerId(1L);
        dto.setRoomId(1L);
        dto.setCheckInDate(LocalDate.of(2026, 6, 3));
        dto.setCheckOutDate(LocalDate.of(2026, 6, 1));

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_shouldReturnFalse_whenRoomIsNotAvailable() {

        BookingDTO dto = new BookingDTO();
        dto.setCustomerId(1L);
        dto.setRoomId(1L);
        dto.setCheckInDate(LocalDate.of(2026, 6, 1));
        dto.setCheckOutDate(LocalDate.of(2026, 6, 3));
        dto.setNumberOfGuests(1);

        when(bookingRepository.existsCustomerBookingConflict(anyLong(), any(), any(), any()))
                .thenReturn(false);

        when(roomService.isRoomAvailable(1L, dto.getCheckInDate(), dto.getCheckOutDate(), 1, null))
                .thenReturn(false);

        boolean result = bookingService.saveBooking(dto);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_shouldReturnTrue_whenBookingIsValid() {

        BookingDTO dto = new BookingDTO();
        dto.setCustomerId(1L);
        dto.setRoomId(1L);
        dto.setCheckInDate(LocalDate.of(2026, 6, 1));
        dto.setCheckOutDate(LocalDate.of(2026, 6, 3));
        dto.setNumberOfGuests(1);

        when(bookingRepository.existsCustomerBookingConflict(anyLong(), any(), any(), any()))
                .thenReturn(false);

        when(roomService.isRoomAvailable(1L, dto.getCheckInDate(), dto.getCheckOutDate(), 1, null))
                .thenReturn(true);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        boolean result = bookingService.saveBooking(dto);

        assertTrue(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void deleteById_shouldReturnFalse_whenBookingDoesNotExist() {

        when(bookingRepository.existsById(99L)).thenReturn(false);

        boolean result = bookingService.deleteById(99L);

        assertFalse(result);
        verify(bookingRepository, never()).deleteById(99L);
    }

    @Test
    void deleteById_shouldReturnTrue_whenBookingExists() {

        when(bookingRepository.existsById(1L)).thenReturn(true);

        boolean result = bookingService.deleteById(1L);

        assertTrue(result);
        verify(bookingRepository, times(1)).deleteById(1L);
    }
}