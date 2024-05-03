package ivan.denysiuk.learntest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.domain.dto.EmployeeDto;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import ivan.denysiuk.learntest.domain.entity.TypeOfContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    EmployeeService employeeService;
    Employee employee;

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

        employee = Employee.builder()
                .id(1L)
                .firstName("Iga")
                .lastName("Kowalska")
                .rate(25.0)
                .typeOfContract(TypeOfContract.PRACA)
                .specialization("Java Developer")
                .dateOfBirthday(LocalDate.of(1990, 5, 3))
                .PESEL("12345678901")
                .ZUSindex("123456789")
                .workedShift(randomShifts.collect(Collectors.toUnmodifiableSet()))
                .build();
    }

    String randomTime() {
        int hour = new Random().nextInt(24);
        int minute = new Random().nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }
    @Test
    void getAllEmployees() throws Exception {
        given(employeeService.getAllEmployees()).willReturn(List.of(employee));

        mockMvc.perform(get("/api/v1/employee")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()",is(1)));
    }

    @Test
    void getEmployeeById() throws Exception {
        given(employeeService.getEmployeeById(employee.getId())).willReturn(employee);

        mockMvc.perform(get("/api/v1/employee/"+ employee.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(Integer.parseInt(employee.getId().toString()))))
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @Test
    void getEmployeeByPESEL() throws Exception {
        given(employeeService.getEmployeeByPESEL(anyString())).willReturn(employee);

        mockMvc.perform(get("/api/v1/employee/searchbypesel/"+ employee.getPESEL())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(Integer.parseInt(employee.getId().toString()))))
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @Test
    void getEmployeeByFullName() throws Exception {
        given(employeeService.getEmployeeByfullName(anyString())).willReturn(employee);

        mockMvc.perform(get("/api/v1/employee/searchbyfullname/"+ employee.getFullName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(Integer.parseInt(employee.getId().toString()))))
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @Test
    void deleteEmployee() throws Exception {
        when(employeeService.deleteEmployee(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/employee/"+ employee.getId() +"/delete")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        verify(employeeService).deleteEmployee(any());
    }

    @Test
    void saveEmployee() throws Exception {
        given(employeeService.saveEmployee(any(EmployeeDto.class))).willReturn(employee);

        mockMvc.perform(post("/api/v1/employee/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee))) // Include the serialized employee object in the request body
                .andExpect(status().isCreated());
    }

    @Test
    void updateEmployee() throws Exception {
        mockMvc.perform(put("/api/v1/employee/"+employee.getId()+"/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        verify(employeeService).updateEmployee(anyLong(),any(EmployeeDto.class));
    }

    @Test
    void PatchEmployee() throws Exception {
        mockMvc.perform(patch("/api/v1/employee/"+employee.getId()+"/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));

        verify(employeeService).patchEmployee(anyLong(),any(EmployeeDto.class));
    }

    @Test
    void employeeSalary() throws Exception {
        when(employeeService.getEmployeeById(anyLong())).thenReturn(employee);
        when(employeeService.getMonthTax(1L, 5)).thenReturn(Map.of("IncomeTax", 200.00));
        when(employeeService.getMonthSalary(1L, 5)).thenReturn(5000.00);

        mockMvc.perform(get("/api/v1/employee/" + 1L + "/salary?month=5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.salary").value(5000.00));
    }
}