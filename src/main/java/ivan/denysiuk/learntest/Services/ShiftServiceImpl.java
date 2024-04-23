package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShiftServiceImpl implements ShiftService {
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * @param shiftId
     * @return
     */
    @Override
    public Shift getShiftById(Long shiftId) {
        return shiftRepository.getShiftById(shiftId);
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public List<Shift> getAllShiftByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)){
            return Collections.emptyList();
        }
        return shiftRepository.getShiftsByEmployeeId(employeeId);
    }

    /**
     * @param employeeId
     * @param from
     * @param to
     * @return
     */
    @Override
    public List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, YearMonth from, YearMonth to) {
        if (!employeeRepository.existsById(employeeId)) {
            return Collections.emptyList();
        }

        List<Shift> shifts = shiftRepository.getShiftsByEmployeeId(employeeId);
        if (shifts.isEmpty()) {
            return Collections.emptyList();
        }

        return shifts.stream()
                .filter(shift -> {
                    LocalDate shiftDate = shift.getDate();
                    YearMonth shiftYearMonth = YearMonth.of(shiftDate.getYear(), shiftDate.getMonthValue());
                    return !shiftYearMonth.isBefore(from) && !shiftYearMonth.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    /**
     * @param shift
     * @return
     */
    @Override
    public Shift addShiftToDatabase(Shift shift) {
        return shiftRepository.save(shift);
    }
    /**
     * @param shiftId
     * @return
     */
    @Override
    public boolean deleteShiftFromDatabase(Long shiftId) {
        if(shiftRepository.existsById(shiftId)){
            shiftRepository.deleteById(shiftId);
            return true;
        }
        return false;
    }

    /**
     * @param shiftId
     * @param employeeId
     * @return 0 means ok
     *         1 means not ok, shift not exist
     *         2 means not ok, employee is null
     */
    @Override
    public int changeEmployee(Long shiftId, Long employeeId) {
        Employee employee = employeeRepository.getEmployeeById(employeeId);
        Shift shift = shiftRepository.getShiftById(shiftId);

        if(shift==null) return 1;
        if(employee == null) return 2;

        shift.setEmployee(employee);
        shiftRepository.save(shift);
        return 0;
    }

    /**
     * Change start time, and end time in the desired work shift
     *
     * @param shiftId id of the desired shift
     * @param startTime of the shift time for which you need to leave
     * @param endTime of the shift time for which you need to leave
     * @return 0 means time changed successfully
     *         1 mean failed, shift not exist
     *         2 means failed, time is not valid
     */
    @Override
    public int changeWorkedTime(Long shiftId, String startTime, String endTime) {
        Shift shift = shiftRepository.getShiftById(shiftId);

        if(shift==null) return 1;
        if(!isTimeValid(startTime) || !isTimeValid(endTime)) return 2;

        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shiftRepository.save(shift);

        return 0;
    }
    /**
     * Check is provided time valid
     *
     * @param Time provided Time which need to check
     * @return true if time valid
     *         false if time mot valid
     */
    @Override
    public boolean isTimeValid(String Time){
        try {
            LocalTime localTime = LocalTime.parse(Time, DateTimeFormatter.ofPattern("HH:mm"));

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Change Actual start time, and end time in the desired work shift
     *
     * @param shiftId id of the desired shift
     * @param startTime of the shift time for which you need to leave
     * @param endTime of the shift time for which you need to leave
     * @return 0 means time changed successfully
     *         1 mean failed, shift not exist
     *         2 means failed, time is not valid
     */
    @Override
    public int changeActualWorkTime(Long shiftId,String startTime, String endTime) {
        Shift shift = shiftRepository.getShiftById(shiftId);

        if(shift==null) return 1;
        if(!isTimeValid(startTime) || !isTimeValid(endTime)) return 2;

        shift.setActualStartTime(startTime);
        shift.setActualEndTime(endTime);
        shiftRepository.save(shift);

        return 0;
    }

    /**
     * count all Shift on database
     *
     * @return number of all shift
     */
    @Override
    public int countAllShift() {
        return Integer.parseInt(String.valueOf(shiftRepository.count()));
    }
}
