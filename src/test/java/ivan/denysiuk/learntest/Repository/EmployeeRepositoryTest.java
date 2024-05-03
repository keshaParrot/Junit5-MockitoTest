package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;
    Employee employee;
    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Adam");
        employee.setLastName("Kowalski");
        employee.setPESEL("02846637281");
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void getEmployeeById_whenEmployeeExistOnDB_notNull() {
        employeeRepository.save(employee);
        Long employeeId = employeeRepository.findAll().get(0).getId();

        Employee expectedEmployee = employeeRepository.getEmployeeById(employeeId);

        Assertions.assertThat(expectedEmployee).isNotNull();
        Assertions.assertThat(expectedEmployee.getId()).isGreaterThan(0);
    }
    @Test
    void getEmployeeByPESEL_whenEmployeeExistOnDB_notNull() {
        employeeRepository.save(employee);

        Employee expectedEmployee = employeeRepository.getEmployeeByPESEL("02846637281");

        Assertions.assertThat(expectedEmployee).isNotNull();
        Assertions.assertThat(expectedEmployee.getId()).isGreaterThan(0);
    }
    @Test
    void getEmployeeByFullName_whenEmployeeExistOnDB_notNull() {
        employeeRepository.save(employee);

        Employee expectedEmployee = employeeRepository.getEmployeeByFullName(employee.getFirstName()+" "+employee.getLastName());

        Assertions.assertThat(expectedEmployee).isNotNull();
        Assertions.assertThat(expectedEmployee.getId()).isGreaterThan(0);
    }
    @Test
    void saveEmployee_whenEmployeeNotNull_notNull() {
        Employee savedShift = employeeRepository.save(employee);

        Assertions.assertThat(savedShift).isNotNull();
        Assertions.assertThat(savedShift.getId()).isGreaterThan(0);
    }

    @Test
    void deleteEmployeeById_whenEmployeeExistOnDB_notNull() {
        employeeRepository.save(employee);
        Long employeeId = employeeRepository.findAll().get(0).getId();

        employeeRepository.deleteById(employeeId);
        assertThrows(NoSuchElementException.class,()->{
            employeeRepository.findById(employeeId).orElseThrow(() -> new NoSuchElementException("Employee with id  " + employeeId + " not found"));
        });
    }

    @Test
    void countAllEmployee_whenEmployeeExistOnDB_one() {
        employeeRepository.save(employee);
        assertEquals(1,employeeRepository.count());
    }
}