/*package Pensionaten.service;

import Pensionaten.dto.CustomerDTO;
import Pensionaten.models.Customer;
import Pensionaten.repositories.BookingRepository;
import Pensionaten.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(9L)
                .firstName("Alex")
                .lastName("Zeljic")
                .email("alex@test.se")
                .phone("0701234567")
                .build();

        //customerDTO = CustomerDTO.builder()
                .id(9L)
                .firstName("Boris")
                .lastName("Ivis")
                .email("boris@test.se")
                .phone("123456789")
                .build();
    }

    @Test
    void findAll_shouldReturnCustomerDTOList() {

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDTO> result = customerService.findAll();

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).getFirstName());
        assertEquals("Zeljic", result.get(0).getLastName());
    }

    @Test
    void findById_shouldReturnCustomerDTO_whenCustomerExists() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals(9L, result.getId());
        assertEquals("Alex", result.getFirstName());
    }

    @Test
    void findById_shouldReturnNull_whenCustomerDoesNotExist() {

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        CustomerDTO result = customerService.findById(99L);

        assertNull(result);
    }

    @Test
    void save_shouldSaveCustomer() {

        customerService.save(customerDTO);

        verify(customerRepository, times(1))
                .save(any(Customer.class));
    }

    @Test
    void delete_shouldReturnFalse_whenCustomerDoesNotExist() {

        when(customerRepository.existsById(1L)).thenReturn(false);

        boolean result = customerService.delete(1L);

        assertFalse(result);

        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_shouldReturnFalse_whenCustomerHasBookings() {

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByCustomerId(1L)).thenReturn(true);

        boolean result = customerService.delete(1L);

        assertFalse(result);

        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_shouldReturnTrue_whenCustomerHasNoBookings() {

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByCustomerId(1L)).thenReturn(false);

        boolean result = customerService.delete(1L);

        assertTrue(result);

        verify(customerRepository, times(1)).deleteById(1L);
    }
}*/