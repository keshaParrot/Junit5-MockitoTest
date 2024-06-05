package ivan.denysiuk.learntest.domains.dtos;

import ivan.denysiuk.learntest.annotations.EmployeeExist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Start time must be in the format HH:MM")
    private String startTime;
    @NotBlank(message = "End time cannot be empty")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "End time must be in the format HH:MM")
    private String endTime;
    private String actualStartTime;
    private String actualEndTime;
    @NotNull(message = "Employee id cannot be empty")
    @EmployeeExist(message = "Employee does not exist")
    Long employeeId;
}
