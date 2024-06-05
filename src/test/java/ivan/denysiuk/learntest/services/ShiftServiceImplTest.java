package ivan.denysiuk.learntest.services;

import ivan.denysiuk.learntest.repositories.EmployeeRepository;
import ivan.denysiuk.learntest.repositories.ShiftRepository;
import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import ivan.denysiuk.learntest.domains.entities.Shift;
import ivan.denysiuk.learntest.domains.mappers.ShiftMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceImplTest {

    @InjectMocks
    ShiftServiceImpl shiftService;
    @Mock
    ShiftRepository shiftRepository;
    @Mock
    EmployeeRepository employeeRepository;
    Shift shift;
    Employee employee;
    Long shiftId = 1L;
    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .build();

        shift = Shift.builder()
                .id(shiftId)
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .employee(employee)
                .build();
    }

    @Test
    void getShiftById() {
        when(shiftRepository.getShiftById(shiftId)).thenReturn(shift);

        Shift expectedShift = shiftService.getShiftById(shiftId);
        Assertions.assertNotNull(expectedShift, "Returned employee should not be null");
    }

    @Test
    void getAllShiftByEmployee_whenShiftExistOnDB_true() {

        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(shiftRepository.getShiftsByEmployeeId(1L)).thenReturn(Arrays.asList(new Shift(), new Shift()));

        List<Shift> expectedShift = shiftService.getAllShiftByEmployee(1L);
        assertEquals(2,expectedShift.size());
    }
    @Test
    void getAllShiftByEmployee_whenShiftNotExistOnDB_false() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(shiftRepository.getShiftsByEmployeeId(1L)).thenReturn(Collections.emptyList());

        List<Shift> expectedShift = shiftService.getAllShiftByEmployee(1L);
        assertTrue(expectedShift.isEmpty());

    }
    @Test
    void getAllShiftByEmployeeFromToDate() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(shiftRepository.getShiftsByEmployeeId(1L)).thenReturn(Arrays.asList(
                Shift.builder()
                        .date(LocalDate.of(2024,1,23))
                        .build(),
                Shift.builder()
                        .date(LocalDate.of(2024,4,23))
                        .build()));

        List<Shift> expectedShift = shiftService.getAllShiftByEmployeeFromToDate(1L, YearMonth.of(2024,2),YearMonth.of(2024,4));
        assertFalse(expectedShift.isEmpty());
    }

    @Test
    void addShift() {
        when(employeeRepository.getEmployeeById(anyLong())).thenReturn(employee);
        shiftService.saveShift(getShiftFromDto(shift));

        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void deleteShift() {
        when(shiftRepository.existsById(1L)).thenReturn(true);

        shiftService.deleteShift(1L);

        verify(shiftRepository, times(1)).deleteById(1L);
    }

    @Test
    void patchShift() {
        ShiftDto newShift = ShiftDto.builder()
                .station("engineer")
                .date(LocalDate.of(2022,2,2))
                .startTime("14:40")
                .endTime("19:50")
                .employeeId(1L)
                .build();

        when(shiftRepository.getShiftById(anyLong())).thenReturn(shift);
        when(shiftService.patchShift(anyLong(), newShift)).thenReturn(shift);
        when(employeeRepository.getEmployeeById(anyLong())).thenReturn(employee);

        Shift updatedShift = shiftService.patchShift(2L,newShift);

        assertEquals(LocalDate.of(2022,2,2), updatedShift.getDate());
        assertEquals("14:40", updatedShift.getStartTime());
        assertEquals("Adam", updatedShift.getEmployee().getFirstName());
        assertEquals("12:30",updatedShift.getActualStartTime());
    }

    @Test
    void updateShift() {
        ShiftDto shiftDto = getShiftFromDto(shift);
        when(employeeRepository.getEmployeeById(anyLong())).thenReturn(employee);

        shiftService.updateShift(1L,shiftDto);

        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void countAllShift() {
        long expectedCount = 5;
        when(shiftRepository.count()).thenReturn(expectedCount);

        long actualCount = shiftService.countAllShift();

        assertEquals(expectedCount, actualCount, "The count of employees should match the expected count");
    }
    ShiftDto getShiftFromDto(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift);
    }
}