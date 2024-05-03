package ivan.denysiuk.learntest.Services.interfaces;

import ivan.denysiuk.learntest.domain.dto.ShiftDto;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service

public interface ShiftService {

    Shift getShiftById(Long shiftId);
    List<Shift> getAllShiftByEmployee(Long employeeId);
    List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, YearMonth From, YearMonth to);
    Shift saveShift(Shift shift);
    boolean deleteShift(Long shiftId);
    Shift patchShift(Long shiftId, ShiftDto updatedShift);

    Shift updateShift(Long shiftId, ShiftDto updatedShift);

    int countAllShift();

}
