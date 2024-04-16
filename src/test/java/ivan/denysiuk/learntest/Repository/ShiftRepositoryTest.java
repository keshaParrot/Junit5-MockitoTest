package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ShiftRepositoryTest {

    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    Shift shift;
    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Adam");

        employeeRepository.save(employee);
        Long employeeId = employeeRepository.findAll().get(0).getId();

        shift = new Shift();
        shift.setId(1L);
        shift.setEmployee(employeeRepository.findById(employeeId).get());
        shift.setActualStartTime("12:30");
        shift.setActualEndTime("22:30");


    }
    @AfterEach
    void tearDown() {
        shiftRepository.deleteAll();
    }

    @Test
    void saveShift_whenShiftExist_Shift() {
        Shift savedShift = shiftRepository.save(shift);

        Assertions.assertThat(savedShift).isNotNull();
        Assertions.assertThat(savedShift.getId()).isGreaterThan(0);
    }

    @Test
    void deleteShift_whenShiftExistOnDB_True() {
        shiftRepository.save(shift);
        Long shiftId = shiftRepository.findAll().get(0).getId();

        shiftRepository.deleteById(shiftId);
        assertThrows(NoSuchElementException.class,()->{
            shiftRepository.findById(shiftId).orElseThrow(() -> new NoSuchElementException("Shift with id  " + shiftId + " not found"));
        });
    }
    @Test
    void getShiftById_whenShiftExistOnDB_Shift() {
        shiftRepository.save(shift);

        Long shiftId = shiftRepository.findAll().get(0).getId();
        Shift shift2 = shiftRepository.getShiftById(shiftId);

        Assertions.assertThat(shift2).isNotNull();
    }
    @Test
    void getShiftByEmployee_whenEmployeeExistOnDB_Shift() {
        shiftRepository.save(shift);


        System.out.println(shiftRepository.findAll());
        System.out.println(employeeRepository.findAll());

        Long employeeId = employeeRepository.findAll().get(0).getId();

        Shift expectedShift = shiftRepository.getShiftByEmployeeId(employeeId);

        Assertions.assertThat(expectedShift).isNotNull();
        Assertions.assertThat(expectedShift.getId()).isGreaterThan(0);
    }

    @Test
    void countShifts_whenShiftExistOnDB_One() {
        shiftRepository.save(shift);
        assertEquals(1,shiftRepository.count());
    }







}