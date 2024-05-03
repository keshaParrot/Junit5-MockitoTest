package ivan.denysiuk.learntest.domain.dto;

import ivan.denysiuk.learntest.domain.entity.Shift;
import ivan.denysiuk.learntest.domain.entity.TypeOfContract;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last Name cannot be empty")
    private String lastName;
    @Min(value = 1, message = "Rate must be at least 1")
    private Double rate;
    @NotNull(message = "Contract type cannot be null")
    private TypeOfContract typeOfContract;
    @NotBlank(message = "Specialization cannot be empty")
    private String specialization;
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirthday;
    @Pattern(regexp = "\\d{11}", message = "Invalid pesel number format")
    private String PESEL;
    @NotBlank(message = "ZUS index cannot be empty")
    private String ZUSindex;
}
