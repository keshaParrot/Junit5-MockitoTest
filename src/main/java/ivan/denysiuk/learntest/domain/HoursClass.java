package ivan.denysiuk.learntest.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@Builder
public class HoursClass {
    private int hours;
    private int minutes;

    public HoursClass(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }
    public HoursClass() {
        this.hours = 0;
        this.minutes = 0;
    }

    public HoursClass add(HoursClass other) {
        int newHours = this.hours + other.hours;
        int newMinutes = this.minutes + other.minutes;

        if (newMinutes >= 60) {
            newHours += newMinutes / 60;
            newMinutes %= 60;
        }

        return new HoursClass(newHours, newMinutes);
    }
    public void setHoursAndMinutes(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
        //normalize();
    }
    public void setHoursAndMinutes(HoursClass other) {
        this.hours = other.getHours();
        this.minutes = other.getMinutes();
        //normalize();
    }
    public HoursClass subtract(HoursClass other) {
        int newHours = this.hours - other.hours;
        int newMinutes = this.minutes - other.minutes;

        if (newMinutes < 0) {
            newHours--;
            newMinutes += 60;
        }

        if (newHours < 0) {
            newHours = 0;
            newMinutes = 0;
        }

        return new HoursClass(newHours, newMinutes);
    }

    @Override
    public String toString() {
        return "HoursClass{" +
                "hours=" + hours +
                ", minutes=" + minutes +
                '}';
    }
}
