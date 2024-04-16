package ivan.denysiuk.learntest.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Double rate;
    private String specialization;
    private LocalDateTime dateOfBirthday;
    private String PESEL;
    private String ZUSindex;
    @OneToMany(mappedBy = "employee")
    Set<Shift> workedShift;

}
