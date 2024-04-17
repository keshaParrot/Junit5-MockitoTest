package ivan.denysiuk.learntest.domain.entity;

import ivan.denysiuk.learntest.domain.HoursClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shift implements Comparable<Shift>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String station;

    private LocalDateTime date;
    private String startTime;
    private String endTime;
    private String actualStartTime;
    private String actualEndTime;
    @ManyToOne
    Employee employee;



    public HoursClass getWorkedTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localStartTime = LocalTime.parse(this.actualStartTime, formatter);
        LocalTime localEndTime = LocalTime.parse(this.actualEndTime, formatter);
        Duration duration;

        if (localEndTime.isBefore(localStartTime)) {
            duration = Duration.between(localStartTime, LocalTime.MAX).plus(Duration.between(LocalTime.MIN, localEndTime));
        } else {
            duration = Duration.between(localStartTime, localEndTime);
        }

        long totalMinutes = duration.toMinutes();
        int hours = (int) (totalMinutes / 60);
        int minutes = (int) (totalMinutes % 60);

        return new HoursClass(hours, minutes);
    }

    @Override
    public int compareTo(Shift o) {
        return 0;
    }
}
