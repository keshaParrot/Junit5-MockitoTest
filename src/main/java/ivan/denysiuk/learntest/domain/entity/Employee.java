package ivan.denysiuk.learntest.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
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

    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
    Set<Shift> workedShift= new HashSet<>();

    public String getFullName() {
        return firstName+" "+lastName;
    }
    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(rate, employee.rate) && typeOfContract == employee.typeOfContract && Objects.equals(specialization, employee.specialization) && Objects.equals(dateOfBirthday, employee.dateOfBirthday) && Objects.equals(PESEL, employee.PESEL) && Objects.equals(ZUSindex, employee.ZUSindex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, rate, typeOfContract, specialization, dateOfBirthday, PESEL, ZUSindex);
    }

    public String safetyToString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", rate=" + rate +
                ", typeOfContract=" + typeOfContract +
                ", specialization='" + specialization + '\'' +
                ", dateOfBirthday=" + dateOfBirthday +
                ", PESEL='" + PESEL + '\'' +
                ", ZUSindex='" + ZUSindex + '\'' +"}";
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
                ", workedShift=" + workedShift.stream()
                .map(Shift::safetyToString)
                .collect(Collectors.joining(", ", "[", "]")) + "}";
    }
}
