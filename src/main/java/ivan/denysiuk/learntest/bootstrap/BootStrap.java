package ivan.denysiuk.learntest.bootstrap;

import ivan.denysiuk.learntest.Repository.EmployeeRepository;
import ivan.denysiuk.learntest.Repository.ShiftRepository;
import ivan.denysiuk.learntest.Services.ShiftServiceImpl;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BootStrap implements CommandLineRunner {

    EmployeeRepository employeeRepository;
    ShiftRepository shiftRepository;
    EmployeeService employeeService;
    ShiftServiceImpl shiftService;

    @Override
    public void run(String... args){
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Kowalski")
                .PESEL("03947283728")
                .build();

        employeeRepository.save(employee);

        Shift shift = Shift.builder()
                .actualStartTime("12:30")
                .actualEndTime("22:30")
                .employee(employee)
                .build();

        shiftRepository.save(shift);
        System.out.println(employeeService.getAllEmployees());
        //employeeService.patchEmployee(1L,Employee.builder()
          //      .PESEL("9999999999")
            //    .build());
        System.out.println(employeeService.getAllEmployees());

    }
}
