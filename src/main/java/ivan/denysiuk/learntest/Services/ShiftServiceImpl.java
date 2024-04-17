package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.entity.Shift;

import java.time.LocalDateTime;
import java.util.List;

public class ShiftServiceImpl implements ShiftService {
    /**
     * @param id
     * @return
     */
    @Override
    public Shift getShiftById(Long id) {
        return null;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public List<Shift> getAllShiftByEmployee(Long employeeId) {
        return null;
    }

    /**
     * @param employeeId
     * @param From
     * @param to
     * @return
     */
    @Override
    public List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, LocalDateTime From, LocalDateTime to) {
        return null;
    }

    /**
     * @param employeeId
     * @param startTime
     * @param endTime
     * @param station
     * @return
     */
    @Override
    public int addShiftToDatabase(Long employeeId, String startTime, String endTime, String station) {
        return 0;
    }

    /**
     * @param shiftId
     * @return
     */
    @Override
    public boolean deleteShiftFromDatabase(Long shiftId) {
        return false;
    }

    /**
     * @param id
     * @param employeeId
     * @return
     */
    @Override
    public int changeEmployee(Long id, Long employeeId) {
        return 0;
    }

    /**
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public int changeWorkTime(Long id, String startTime, String endTime) {
        return 0;
    }

    /**
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public int changeActualWorkTime(String startTime, String endTime) {
        return 0;
    }
}
