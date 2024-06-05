package ivan.denysiuk.learntest.domains;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Represents a time duration in hours and minutes.
 * This class provides methods for adding, subtracting, and setting hours and minutes.
 */
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

    /**
     * Sets the hours and minutes of the time.
     *
     * @param hours the hours to set
     * @param minutes the minutes to set
     */
    public void setHoursAndMinutes(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
        //normalize();
    }

    /**
     * Sets the hours and minutes of the time.
     *
     * @param other the other hours class
     */
    public void setHoursAndMinutes(HoursClass other) {
        this.hours = other.getHours();
        this.minutes = other.getMinutes();
        //normalize();
    }

    /**
     * Adds the hours and minutes of another HoursClass object to this HoursClass object.
     * If the sum of minutes exceeds 60, adjusts the hours accordingly.
     *
     * @param other the HoursClass object to add
     * @return a new HoursClass object representing the result of addition
     */
    public HoursClass add(HoursClass other) {
        int newHours = this.hours + other.hours;
        int newMinutes = this.minutes + other.minutes;

        if (newMinutes >= 60) {
            newHours += newMinutes / 60;
            newMinutes %= 60;
        }

        return new HoursClass(newHours, newMinutes);
    }
    /**
     * Subtracts the hours and minutes of another HoursClass object from this HoursClass object.
     * If the result of subtraction leads to negative minutes, adjusts the hours accordingly.
     * If the result of subtraction leads to negative hours, returns a zeroed HoursClass object.
     *
     * @param other the HoursClass object to subtract
     * @return a new HoursClass object representing the result of subtraction
     */
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
