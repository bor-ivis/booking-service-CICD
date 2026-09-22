package Pensionaten.controller;
import org.springframework.beans.factory.annotation.Value;
import Pensionaten.dto.BookingDTO;
import Pensionaten.dto.CustomerDTO;
import Pensionaten.service.BookingService;
import Pensionaten.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

// Controller som hanterar bokningar i webbgränssnittet
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final RestTemplate restTemplate;
    private final RoomService roomService;
    private final BookingService bookingService;
    @Value("${customer.service.url}")
    private String customerServiceUrl;

    //Visar alla kunder i bokningssidan
    private List<CustomerDTO> fetchAllCustomers() {
        try {
            CustomerDTO[] customers = restTemplate.getForObject(customerServiceUrl, CustomerDTO[].class);
            return customers != null ? List.of(customers) : Collections.emptyList();
        } catch (ResourceAccessException e) {
            return null;
        }
    }

    // Visar alla bokningar
    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.findAll());
        return "customers/bookings/list";
    }

    // Öppnar formuläret för att skapa en ny bokning
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new BookingDTO());

        List<CustomerDTO> customers = fetchAllCustomers();
        if (customers == null) {
            model.addAttribute("customers", Collections.emptyList());
            model.addAttribute("error", "Kunde inte hämta gästlistan just nu. Försök igen senare.");
        } else {
            model.addAttribute("customers", customers);
        }

        model.addAttribute("rooms", List.of());
        return "customers/bookings/form";
    }

    // Söker fram lediga rum baserat på datum och antal gäster
    @PostMapping("/search")
    public String searchRooms(@ModelAttribute("booking") BookingDTO bookingDTO, Model model) {

        List<CustomerDTO> customers = fetchAllCustomers();
        if (customers == null) {
            model.addAttribute("customers", Collections.emptyList());
            model.addAttribute("error", "Kunde inte hämta gästlistan just nu. Försök igen senare.");
        } else {
            model.addAttribute("customers", customers);
        }

        if (bookingDTO.getCheckInDate() != null &&
                bookingDTO.getCheckOutDate() != null &&
                bookingDTO.getNumberOfGuests() > 0) {

            model.addAttribute("rooms", roomService.findAvailableRooms(
                    bookingDTO.getCheckInDate(),
                    bookingDTO.getCheckOutDate(),
                    bookingDTO.getNumberOfGuests(),
                    bookingDTO.getId()
            ));
        } else {
            model.addAttribute("rooms", List.of());
            model.addAttribute("error", "Fyll i datum och antal personer först.");
        }

        model.addAttribute("booking", bookingDTO);
        return "customers/bookings/form";
    }

    // Sparar en ny bokning eller uppdaterar en befintlig bokning
    @PostMapping("/save")
    public String saveBooking(@Valid @ModelAttribute("booking") BookingDTO bookingDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("customers", fetchAllCustomers());
            model.addAttribute("rooms", List.of());
            return "customers/bookings/form";
        }

        boolean success;
        try {
            success = bookingService.saveBooking(bookingDTO);
        } catch (ResourceAccessException e) {
            model.addAttribute("error", "Bokningen kunde inte hanteras just nu. Försök igen senare");
            model.addAttribute("customers", fetchAllCustomers());
            model.addAttribute("rooms", List.of());
            return "customers/bookings/form";
        }

        if (!success) {
            model.addAttribute("error", "Bokningen kunde inte sparas. Gästen har redan en bokning under dessa datum eller så är rummet upptaget");
            model.addAttribute("customers", fetchAllCustomers());
            model.addAttribute("rooms", roomService.findAvailableRooms(
                    bookingDTO.getCheckInDate(),
                    bookingDTO.getCheckOutDate(),
                    bookingDTO.getNumberOfGuests(),
                    bookingDTO.getId()
            ));
            return "customers/bookings/form";
        }

        redirectAttributes.addFlashAttribute("message", "Bokningen sparades.");
        return "redirect:/bookings";
    }

    // Öppnar formuläret med en befintlig bokning för redigering
    @GetMapping("/change/{id}")
    public String showChangeForm(@PathVariable Long id, Model model) {
        BookingDTO bookingDTO = bookingService.findById(id);

        if (bookingDTO == null) {
            return "redirect:/bookings";
        }

        model.addAttribute("booking", bookingDTO);

        List<CustomerDTO> customers = fetchAllCustomers();
        if (customers == null) {
            model.addAttribute("customers", Collections.emptyList());
            model.addAttribute("error", "Kunde inte hämta gästlistan just nu. Försök igen senare.");
        } else {
            model.addAttribute("customers", customers);
        }

        model.addAttribute("rooms", roomService.findAvailableRooms(
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate(),
                bookingDTO.getNumberOfGuests(),
                bookingDTO.getId()
        ));

        return "customers/bookings/form";
    }

    // Tar bort / avbokar en bokning
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = bookingService.deleteById(id);

        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Bokningen togs bort.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Kunde inte ta bort bokningen.");
        }

        return "redirect:/bookings";
    }
}