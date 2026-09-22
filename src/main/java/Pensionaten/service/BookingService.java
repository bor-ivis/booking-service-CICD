package Pensionaten.service;

import Pensionaten.dto.BookingDTO;
import Pensionaten.dto.CustomerDTO;
import Pensionaten.models.Booking;
import Pensionaten.repositories.BookingRepository;

import Pensionaten.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.util.List;

// Serviceklass som innehåller affärslogik för bokningar.
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    //private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final RestTemplate restTemplate;
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:8081/api/customers";

    // Hämtar alla bokningar och gör om dem från Entity till DTO
    public List<BookingDTO> findAll() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Hämtar en specifik bokning på id
    public BookingDTO findById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public boolean saveBooking(BookingDTO dto) {

        // Kontrollerar att kund och rum är valda
        if (dto.getCustomerId() == null || dto.getRoomId() == null) {
            return false;
        }

        // Kontrollerar att datum är ifyllda
        if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
            return false;
        }

        // Kontrollerar att utcheckning är efter incheckning
        if (!dto.getCheckOutDate().isAfter(dto.getCheckInDate())) {
            return false;
        }

        // Stoppar kunden från att boka flera vistelser under samma datum
        boolean customerConflict = bookingRepository.existsCustomerBookingConflict(
                dto.getCustomerId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate(),
                dto.getId()
        );

        // Returnerar false om kunden redan har en bokning
        if (customerConflict) {
            return false;
        }

        // Kontrollerar att rummet är ledigt och inte dubbelbokat
        boolean available = roomService.isRoomAvailable(
                dto.getRoomId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate(),
                dto.getNumberOfGuests(),
                dto.getId()
        );

        if (!available) {
            return false;
        }

        Booking booking = dto.getId() != null
                ? bookingRepository.findById(dto.getId()).orElse(new Booking())
                : new Booking();

        booking.setCustomerId(dto.getCustomerId());
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());

        bookingRepository.save(booking);
        return true;
    }

    // Tar bort en bokning om den finns
    public boolean deleteById(Long id) {
        if (!bookingRepository.existsById(id)) {
            return false;
        }

        bookingRepository.deleteById(id);
        return true;
    }

    // Gör om Booking Entity till BookingDTO som används i controller och vyer
    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();

        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomerId());

        try {
            CustomerDTO customer = restTemplate.getForObject(
                    CUSTOMER_SERVICE_URL + "/" + booking.getCustomerId(), CustomerDTO.class);
            if (customer != null) {
                dto.setCustomerFirstName(customer.getFirstName());
                dto.setCustomerLastName(customer.getLastName());
            }
        } catch (HttpClientErrorException.NotFound e) {
            dto.setCustomerFirstName("Okänd");
            dto.setCustomerLastName("kund");
        } catch (ResourceAccessException e) {
            dto.setCustomerFirstName("Kunde inte hämtas");
            dto.setCustomerLastName("");
        }
        /*dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerFirstName(booking.getCustomer().getFirstName());
        dto.setCustomerLastName(booking.getCustomer().getLastName());*/

        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        dto.setRoomType(booking.getRoom().getRoomType().getDisplayName());
        dto.setRoomPricePerNight(booking.getRoom().getPricePerNight());

        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());

        // Räknar ut antal nätter och totalpris för bokningen
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        dto.setNights(nights);
        dto.setTotalPrice((int) nights * booking.getRoom().getPricePerNight());

        return dto;
    }
    //Kollar om kunden finns genom rest anrop till customer-service
    private boolean customerExists(Long customerId) {
        try {
            restTemplate.getForObject(CUSTOMER_SERVICE_URL + "/" + customerId, CustomerDTO.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}