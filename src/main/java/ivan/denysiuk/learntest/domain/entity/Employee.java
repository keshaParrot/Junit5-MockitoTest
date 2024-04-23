package ivan.denysiuk.learntest.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime dateOfBirthday;
    private String PESEL;
    private String ZUSindex;
    @OneToMany(mappedBy = "employee",fetch = FetchType.EAGER)
    Set<Shift> workedShift = new HashSet<>();

    @Override
    public String toString() {
        Set<Long> listOfShiftIds = workedShift.stream()
                .map(Shift::getId)
                .collect(Collectors.toSet());
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
                ", workedShift=" + listOfShiftIds +
                '}';
    }
}
