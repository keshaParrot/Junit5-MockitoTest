package ivan.denysiuk.learntest.domains.entities;

import ivan.denysiuk.learntest.domains.HoursClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shift implements Comparable<Shift>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String station;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String actualStartTime;
    private String actualEndTime;
    @ManyToOne(fetch = FetchType.EAGER)
    Employee employee;

    public HoursClass getWorkedTime() {
        if (this.actualStartTime == null || this.actualEndTime == null) {
            return null; 
        }

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

    public String safetyToString() {
        return "Shift{" +
                "id=" + id +
                ", station='" + station + '\'' +
                ", date=" + date +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", actualStartTime='" + actualStartTime + '\'' +
                ", actualEndTime='" + actualEndTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(id, shift.id) && Objects.equals(station, shift.station) && Objects.equals(date, shift.date) && Objects.equals(startTime, shift.startTime) && Objects.equals(endTime, shift.endTime) && Objects.equals(actualStartTime, shift.actualStartTime) && Objects.equals(actualEndTime, shift.actualEndTime) && Objects.equals(employee, shift.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, date, startTime, endTime, actualStartTime, actualEndTime, employee);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", station='" + station + '\'' +
                ", date=" + date +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", actualStartTime='" + actualStartTime + '\'' +
                ", actualEndTime='" + actualEndTime + '\'' +
                (employee!=null? ", employee=" + employee.safetyToString() +
                '}':"}");
    }
}
