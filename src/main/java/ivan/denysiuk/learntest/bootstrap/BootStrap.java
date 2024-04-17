package ivan.denysiuk.learntest.bootstrap;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class BootStrap implements CommandLineRunner {

    EmployeeRepository employeeRepository;
    ShiftRepository shiftRepository;
    EmployeeService employeeService;

    @Override
    public void run(String... args) throws Exception {
        Shift shift = Shift.builder()
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .build();

        shiftRepository.save(shift);

        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .workedShift(Collections.singleton(shift))
                .build();

        employeeRepository.save(employee);

        System.out.println(employeeService.getMonthTax(employee.getId()));
        System.out.println(employeeService.getMonthRevenue(employee.getId()));
    }
}
