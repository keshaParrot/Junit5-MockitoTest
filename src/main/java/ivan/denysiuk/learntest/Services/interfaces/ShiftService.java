package ivan.denysiuk.learntest.Services.interfaces;

import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface ShiftService {

    Shift getShiftById(Long id);
    List<Shift> getAllShiftByEmployee(Long employeeId);
    List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, LocalDateTime From, LocalDateTime to);
    int addShiftToDatabase(Long employeeId,String startTime,String endTime,String station);
    boolean deleteShiftFromDatabase(Long shiftId);
    int changeEmployee(Long id,Long employeeId);
    int changeWorkTime(Long id, String startTime,String endTime);
    int changeActualWorkTime(String startTime,String endTime);

}
