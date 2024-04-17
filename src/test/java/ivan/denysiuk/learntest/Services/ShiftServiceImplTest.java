package ivan.denysiuk.learntest.Services;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceImplTest {

    @InjectMocks
    EmployeeServiceImpl employeeService;
    @Mock
    EmployeeRepository employeeRepository;

    Shift shift;
    @BeforeEach
    void setUp() {
        Employee employee = Employee.builder()
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .rate(23.5)
                .build();

        shift = Shift.builder()
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .employee(employee)
                .build();
    }

    @Test
    void getShiftById_whenShiftExistOnDB_shift() {

    }

    @Test
    void getAllShiftByEmployee_whenShiftExistOnDB_shift() {


    }

    @Test
    void getAllShiftByEmployeeFromToDate_whenShiftExistOnDB_shift() {

    }

    @Test
    void addShiftToDatabase() {

    }

    @Test
    void deleteShiftFromDatabase() {

    }

    @Test
    void changeEmployee() {

    }

    @Test
    void changeWorkTime() {

    }

    @Test
    void changeActualWorkTime() {

    }
}