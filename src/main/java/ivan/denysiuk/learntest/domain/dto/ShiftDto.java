package ivan.denysiuk.learntest.domain.dto;

import ivan.denysiuk.learntest.domain.entity.Employee;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Station cannot be empty")
    private String station;
    @NotNull(message = "Date cannot be empty")
    private LocalDate date;
    @NotBlank(message = "Start time cannot be empty")
    private String startTime;
    @NotBlank(message = "End time cannot be empty")
    private String endTime;
    private String actualStartTime;
    private String actualEndTime;
    @NotNull(message = "Employee id cannot be empty")
    Long employeeId; //TODO we need to change here Employee class on Long
}
