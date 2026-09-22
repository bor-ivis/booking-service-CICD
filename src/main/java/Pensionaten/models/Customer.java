/*package Pensionaten.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

// Entity som representerar en kund/gäst i databasen
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    // Validerar att telefonnumret följer ett rimligt format
    @Pattern(regexp = "^\\+?[0-9]{1,4}?[ .-]?[0-9]{6,12}$")
    private String phone;
}*/