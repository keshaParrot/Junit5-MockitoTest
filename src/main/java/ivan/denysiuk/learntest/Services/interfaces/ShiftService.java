package ivan.denysiuk.learntest.Services.interfaces;

import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service

public interface ShiftService {

    Shift getShiftById(Long shiftId);
    List<Shift> getAllShiftByEmployee(Long employeeId);
    List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, YearMonth From, YearMonth to);
    Shift addShiftToDatabase(Shift shift);
    boolean deleteShiftFromDatabase(Long shiftId);
    int changeEmployee(Long shiftId,Long employeeId);
    int changeWorkedTime(Long shiftId, String startTime,String endTime);

    boolean isTimeValid(String Time);

    int changeActualWorkTime(Long shiftId, String startTime, String endTime);
    int countAllShift();

}
