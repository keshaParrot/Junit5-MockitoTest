package ivan.denysiuk.learntest.services;

import ivan.denysiuk.learntest.repositories.EmployeeRepository;
import ivan.denysiuk.learntest.repositories.ShiftRepository;
import ivan.denysiuk.learntest.services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import ivan.denysiuk.learntest.domains.entities.Shift;
import ivan.denysiuk.learntest.domains.mappers.ShiftMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service giving access to Shifts entity repository
 * Service implement different methods for getting, saving, update shifts to database.
 */
@Service
@AllArgsConstructor
public class ShiftServiceImpl implements ShiftService {
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Retrieves a shift by its ID.
     *
     * @param shiftId the ID of the shift to retrieve
     * @return the found Shift object, or null if no shift is found with the provided ID
     */
    @Override
    public Shift getShiftById(Long shiftId) {
        return shiftRepository.getShiftById(shiftId);
    }

    /**
     * Retrieves all shifts assigned to a specific employee.
     *
     * @param employeeId the ID of the employee
     * @return a list of Shift objects; if the employee does not exist or has no shifts, returns an empty list
     */
    @Override
    public List<Shift> getAllShiftByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)){
            return Collections.emptyList();
        }
        return shiftRepository.getShiftsByEmployeeId(employeeId);
    }

    /**
     * Retrieves all shifts for a specific employee within a specified date range.
     *
     * @param employeeId the ID of the employee
     * @param from the starting YearMonth of the date range
     * @param to the ending YearMonth of the date range
     * @return a list of shifts within the specified date range; returns an empty list if no shifts are found
     */
    @Override
    public List<Shift> getAllShiftByEmployeeFromToDate(Long employeeId, YearMonth from, YearMonth to) {
        if (!employeeRepository.existsById(employeeId)) {
            return Collections.emptyList();
        }

        List<Shift> shifts = shiftRepository.getShiftsByEmployeeId(employeeId);
        if (shifts == null) {
            shifts = Collections.emptyList();
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
     * Saves a new shift or updates an existing one based on the provided ShiftDto.
     *
     * @param shiftDto the data transfer object containing shift details
     * @return the persisted Shift object
     */
    @Override
    public Shift saveShift(ShiftDto shiftDto) {
        Shift shift = getShiftFromDto(shiftDto);
        Employee employee = employeeRepository.getEmployeeById(shiftDto.getEmployeeId());
        shift.setEmployee(employee);
        return shiftRepository.save(shift);
    }
    /**
     * Deletes a shift by its ID.
     *
     * @param shiftId the ID of the shift to delete
     * @return true if the shift was deleted, false otherwise
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
     * Updates specific fields of a shift.
     *
     * @param shiftId the ID of the shift to update
     * @param updatedShift a ShiftDto containing the updated fields
     * @return the updated Shift object
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
        if(updatedShift.getEmployeeId() != null){
            shift.setEmployee(employeeRepository.getEmployeeById(updatedShift.getEmployeeId()));
        }
        return shiftRepository.save(shift);
    }
    /**
     * Updates all fields of a shift.
     *
     * @param shiftId the ID of the shift to update
     * @param updatedShift a ShiftDto containing the new shift data
     * @return the updated Shift object, or null if no shift was found with the given ID
     */
    @Override
    public Shift updateShift(Long shiftId, ShiftDto updatedShift){
        if(!shiftRepository.existsById(shiftId)){
            return null;
        }
        updatedShift.setId(shiftId);
        Shift shift = getShiftFromDto(updatedShift);
        shift.setEmployee(employeeRepository.getEmployeeById(updatedShift.getEmployeeId()));
        return shiftRepository.save(shift);
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
    /**
     * Converts a ShiftDto to a Shift entity.
     *
     * @param shiftDto the ShiftDto to convert
     * @return the converted Shift entity
     */
    private Shift getShiftFromDto(ShiftDto shiftDto){
        return ShiftMapper.INSTANCE.DtoToShift(shiftDto);
    }
}
