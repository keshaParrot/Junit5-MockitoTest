package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.dto.ShiftDto;
import ivan.denysiuk.learntest.domain.entity.Shift;
import ivan.denysiuk.learntest.domain.mapper.ShiftMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
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
    public Shift saveShift(Shift shift) {
        return shiftRepository.save(shift);
    }
    /**
     * @param shiftId
     * @return
     */
    @Override
    public boolean deleteShift(Long shiftId) {
        if(shiftRepository.existsById(shiftId)){
            shiftRepository.deleteById(shiftId);
            return true;
        }
        return false;
    }

    /**
     * @param shiftId
     * @param updatedShift
     * @return
     */
    @Override
    public Shift patchShift(Long shiftId, ShiftDto updatedShift){
        Shift shift = shiftRepository.getShiftById(shiftId);

        if(StringUtils.hasText(updatedShift.getStation())){
            shift.setStation(updatedShift.getStation());
        }
        if(updatedShift.getDate() != null){
            shift.setDate(updatedShift.getDate());
        }
        if(StringUtils.hasText(updatedShift.getStartTime())){
            shift.setStartTime(updatedShift.getStartTime());
        }
        if(StringUtils.hasText(updatedShift.getEndTime())){
            shift.setEndTime(updatedShift.getEndTime());
        }
        if(StringUtils.hasText(updatedShift.getActualStartTime())){
            shift.setActualStartTime(updatedShift.getActualStartTime());
        }
        if(StringUtils.hasText(updatedShift.getActualEndTime())){
            shift.setActualEndTime(updatedShift.getActualEndTime());
        }
        if(updatedShift.getEmployee() != null){
            shift.setEmployee(getShiftFromDto(updatedShift).getEmployee());
        }
        return shiftRepository.save(shift);
    }
    @Override
    public Shift updateShift(Long shiftId, ShiftDto updatedShift){
        updatedShift.setId(shiftId);
        return shiftRepository.save(getShiftFromDto(updatedShift));
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
    private Shift getShiftFromDto(ShiftDto shift){
        return ShiftMapper.INSTANCE.DtoToShift(shift);
    }
}
