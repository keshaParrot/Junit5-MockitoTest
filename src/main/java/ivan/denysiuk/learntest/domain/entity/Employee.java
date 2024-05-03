package ivan.denysiuk.learntest.domain.entity;

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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Double rate;
    private TypeOfContract typeOfContract;
    private String specialization;
    private LocalDate dateOfBirthday;
    private String PESEL;
    private String ZUSindex;

    @OneToMany(mappedBy = "employee",fetch = FetchType.EAGER)
    Set<Shift> workedShift = new HashSet<>();

    public String getFullName() {
        return firstName+" "+lastName;
    }
    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", rate=" + rate +
                ", typeOfContract=" + typeOfContract +
                ", specialization='" + specialization + '\'' +
                ", dateOfBirthday=" + dateOfBirthday +
                ", PESEL='" + PESEL + '\'' +
                ", ZUSindex='" + ZUSindex + '\'' +
                ", workedShift=" + (workedShift != null ? workedShift.stream()
                .map(Shift::toString)
                .collect(Collectors.joining(", ")) : "No shifts") +
                '}';
    }
}
