package Pensionaten.controller;

import Pensionaten.dto.CustomerDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

// Controller som hanterar kunder/gäster
@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:8081/api/customers";


    // Visar alla registrerade kunder
    @GetMapping
    public String listCustomers(Model model) {
        try {
            CustomerDTO[] customers = restTemplate.getForObject(CUSTOMER_SERVICE_URL, CustomerDTO[].class);
            model.addAttribute("customers", customers != null ? List.of(customers) : Collections.emptyList());
        } catch (ResourceAccessException e) { //Tjänsten ska inte krascha om customer-service är nere
            logger.error("Kundtjänsten svarar inte: {}", e.getMessage());
            model.addAttribute("customers", Collections.emptyList());
            model.addAttribute("error", "Kunde inte hämta kunder just nu. Försök igen senare.");
        }
        return "customers/list";
    }

    // Öppnar formuläret för att skapa en ny kund
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "customers/form";
    }

    // Sparar en ny kund eller uppdaterar en befintlig kund
    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customer") CustomerDTO customerDTO,
                               BindingResult result) {

        if (result.hasErrors()) {
            return "customers/form";
        }
        try {
            if (customerDTO.getId() == null) {
                restTemplate.postForObject(CUSTOMER_SERVICE_URL, customerDTO, CustomerDTO.class);
            } else {
                restTemplate.put(CUSTOMER_SERVICE_URL + "/" + customerDTO.getId(), customerDTO);
            }
        } catch (ResourceAccessException e) {
            logger.error("Kundtjänsten svarar inte: {}", e.getMessage());
            return "customers/form";
        }

        return "redirect:/customers";
    }


    // Öppnar formuläret med en befintlig kund för redigering
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            CustomerDTO customer = restTemplate.getForObject(CUSTOMER_SERVICE_URL + "/" + id, CustomerDTO.class);
            if (customer == null) {
                return "redirect:/customers";
            }
            model.addAttribute("customer", customer);
            return "customers/form";
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Kund med id {} hittades inte", id);
            return "redirect:/customers";
        } catch (ResourceAccessException e) {
            logger.error("Kundtjänsten svarar inte: {}", e.getMessage());
            return "redirect:/customers";
        }
    }

    // Tar bort en kund om kunden inte har några bokningar
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            restTemplate.delete(CUSTOMER_SERVICE_URL + "/" + id);
            redirect.addFlashAttribute("message", "Kunden är borttagen.");
        } catch (HttpClientErrorException.Conflict e) {
            logger.warn("Kund med id {} kunde inte tas bort, har aktiva bokningar", id);
            redirect.addFlashAttribute("error", "Kunden kunde inte tas bort eftersom kunden har bokningar.");
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Kund med id {} hittades inte", id);
            redirect.addFlashAttribute("error", "Kunden kunde inte hittas.");
        } catch (ResourceAccessException e) {
            logger.error("Kundtjänsten svarar inte: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Kunde inte ta bort kunden just nu. Försök igen senare.");
        }
        return "redirect:/customers";
    }
}