package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @InjectMocks
    EmployeeServiceImpl employeeService;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    ShiftRepository shiftRepository;

    Employee employee1;
    Employee employee2;
    @BeforeEach
    void setUp() {
        Shift shift = Shift.builder()
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .build();

        //add shift here

        employee1 = Employee.builder()
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .workedShift(Collections.singleton(shift))
                .build();

        employee2 = Employee.builder()
                .firstName("Iga")
                .lastName("Nowak")
                .rate(23.5)
                .build();
    }

    @Test
    void getAllEmployees_whenEmployeesExistOnDB_notNull() {
        List<Employee> savedEmployee = List.of(employee1, employee2);

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
        employeeService.addEmployeeToDatabase(employee2);

        verify(employeeRepository, times(1)).save(employee2);
    }

    @Test
    void updateEmployeeOnDatabase_whenEmployeeNotNull_notNull() {
        Employee employeeToUpdate = employee2;
        employeeToUpdate.setPESEL("94637289402");

        employeeService.updateEmployeeOnDatabase(employeeToUpdate);

        verify(employeeRepository, times(1)).save(employeeToUpdate);
    }

    @Test
    void deleteEmployeeFromDatabase_whenEmployeeExistOnDB_notNull() {
        long employeeIdToDelete = 1;
        when(employeeRepository.existsById(employeeIdToDelete)).thenReturn(true); // Працівник існує у базі

        employeeService.deleteEmployeeFromDatabase(employeeIdToDelete);

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

        double monthSalary = employeeService.getMonthSalary(employeeId);

        Assertions.assertTrue(monthSalary > 0, "The month salary should be greater than 0");
    }
    @Test
    void getMonthTax_whenEmployeesExistOnDB_notNull() {

    }
    @Test
    void getMonthRevenue_whenEmployeesExistOnDB_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee1);

        double monthSalary = employeeService.getMonthSalary(employeeId);

        Assertions.assertTrue(monthSalary > 0.0, "The month salary should be greater than 0");
    }
    @Test
    void getMonthRevenue_whenEmployeesHasNotShift_notNull() {
        Long employeeId = 1L;

        when(employeeRepository.getEmployeeById(employeeId)).thenReturn(employee2);


        double monthSalary = employeeService.getMonthSalary(employeeId);

        Assertions.assertTrue(monthSalary == 0, "The month salary should to be equals 0");
    }
}