package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.domain.dto.EmployeeDto;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import ivan.denysiuk.learntest.domain.mapper.EmployeeMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @InjectMocks
    EmployeeServiceImpl employeeService;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    EmployeeMapper employeeMapper;
    Employee employee1;
    EmployeeDto employee2;
    @BeforeEach
    void setUp() {
        Stream<Shift> randomShifts = Stream.generate(() -> {
            String startTime = randomTime();
            String endTime = randomTime();
            return Shift.builder()
                    .station("Station " + (new Random().nextInt(10) + 1))
                    .date(LocalDate.of(2024,5,5))
                    .startTime(startTime)
                    .endTime(endTime)
                    .actualStartTime(startTime)
                    .actualEndTime(endTime)
                    .employee(null)
                    .build();
        }).limit(5);

        Shift shift = Shift.builder()
                .date(LocalDate.of(2024, 4, 20))
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .build();

        employee1 = Employee.builder()
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .workedShift(randomShifts.collect(Collectors.toUnmodifiableSet()))
                .build();

        employee2 = EmployeeDto.builder()
                .firstName("Iga")
                .lastName("Nowak")
                .rate(23.5)
                .build();
    }

    String randomTime() {
        int hour = new Random().nextInt(24);
        int minute = new Random().nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }

    @Test
    void getAllEmployees_whenEmployeesExistOnDB_notNull() {
        List<Employee> savedEmployee = List.of(employee1);

        Mockito.when(employeeRepository.findAll()).thenReturn(savedEmployee);

        List<Employee> allEmployee = employeeService.getAllEmployees();
        assertEquals(savedEmployee.size(),allEmployee.size());
    }

    @Test
    void getEmployeeById_whenEmployeeExistOnDB_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee1);

        Employee expectedEmployee = employeeService.getEmployeeById(employeeId);
        Assertions.assertNotNull(expectedEmployee, "Returned employee should not be null");
    }

    @Test
    void getEmployeeByPESEL_whenEmployeeExistOnDB_notNull() {
        when(employeeRepository.getEmployeeByPESEL("03947283728")).thenReturn(employee1);

        Employee expectedEmployee = employeeService.getEmployeeByPESEL("03947283728");
        Assertions.assertNotNull(expectedEmployee, "Returned employee should not be null");
    }

    @Test
    void addEmployeeToDatabase_whenEmployeeNotNull_notNull() {
        Employee convertedEmployee = employee1;
        when(employeeMapper.DtoToEmployee(employee2)).thenReturn(convertedEmployee);
        when(employeeRepository.save(convertedEmployee)).thenReturn(employee1);

        Employee result = employeeService.saveEmployee(employee2);

        assertEquals(employee1, result);
        verify(employeeRepository).save(convertedEmployee);
        verify(employeeMapper).DtoToEmployee(employee2);
    }

    @Test
    void updateEmployeeOnDatabase_whenEmployeeNotNull_notNull() {
        EmployeeDto employeeToUpdate = employee2;
        employeeToUpdate.setPESEL("94637289402");
        when(employeeRepository.getEmployeeById(anyLong())).thenReturn(employee1);

        employeeService.updateEmployee(anyLong(),employeeToUpdate);

        verify(employeeRepository, times(1)).save(employee1);
    }
    @Test
    void patchEmployee(){
        EmployeeDto employeeToUpdate = employee2;
        when(employeeRepository.getEmployeeById(anyLong())).thenReturn(employee1);

        employeeService.patchEmployee(anyLong(),employeeToUpdate);

        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void deleteEmployeeFromDatabase_whenEmployeeExistOnDB_notNull() {
        long employeeIdToDelete = 1;
        when(employeeRepository.existsById(employeeIdToDelete)).thenReturn(true);

        employeeService.deleteEmployee(employeeIdToDelete);

        verify(employeeRepository, times(1)).deleteById(employeeIdToDelete);
    }

    @Test
    void countAllEmployees_whenEmployeesExistOnDB_notNull() {
        long expectedCount = 5;
        when(employeeRepository.count()).thenReturn(expectedCount);

        long actualCount = employeeService.countAllEmployees();

        assertEquals(expectedCount, actualCount, "The count of employees should match the expected count");

    }

    @Test
    void getMonthSalaryEmployee_whenEmployeesExistOnDB_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee1);

        double monthSalary = employeeService.getMonthSalary(employeeId,4);

        Assertions.assertTrue(monthSalary > 0, "The month salary should be greater than 0");
    }
    @Test
    void getMonthTax_whenEmployeesExistOnDB_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee1);

        Map<String, Double> mapOfTax = employeeService.getMonthTax(employeeId,4);

        assertNotNull(mapOfTax,"The month salary should be not null");
    }
    @Test
    void getMonthRevenue_whenEmployeesExistOnDB_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee1);

        double monthRevenue = employeeService.getMonthRevenue(employeeId,4);

        Assertions.assertTrue(monthRevenue > 0.0, "The month salary should be greater than 0");
    }
}