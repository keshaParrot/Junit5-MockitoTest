package ivan.denysiuk.learntest.services.interfaces;

import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Shift;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service

public interface ShiftService {

    Shift getShiftById(Long shiftId);
    List<Shift> getAllShiftByEmployee(Long employeeId);
    List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, YearMonth From, YearMonth to);
    Shift saveShift(ShiftDto shift);
    boolean deleteShift(Long shiftId);
    Shift patchShift(Long shiftId, ShiftDto updatedShift);
    Shift updateShift(Long shiftId, ShiftDto updatedShift);
    int countAllShift();

}
