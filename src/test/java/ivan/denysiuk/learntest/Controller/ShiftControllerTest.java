package ivan.denysiuk.learntest.Controller;

import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
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

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShiftController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ShiftControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ShiftService shiftService;
    Shift shift;

    @BeforeEach
    void setUp() {
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Adam")
                .lastName("Ploch")
                .build();

        shift = Shift.builder()
                .id(1L)
                .startTime("22:30")
                .endTime("6:00")
                .date(LocalDate.now())
                .employee(employee)
                .build();
    }
    @Test
    void getShiftById() throws Exception {
        given(shiftService.getShiftById(shift.getId())).willReturn(shift);

        mockMvc.perform(get("/api/v1/shift/"+ shift.getId())
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id",is(Integer.parseInt(shift.getId().toString()))))
                        .andExpect(jsonPath("$.startTime", is(shift.getStartTime())));
    }

    @Test
    void getAllShiftByEmployeeId() throws Exception {
        Long employeeId = 1L;
        given(shiftService.getAllShiftByEmployee(employeeId)).willReturn(List.of(shift));

        mockMvc.perform(get("/api/v1/shift/searchbyemployee/"+ employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id",is(Integer.parseInt(shift.getId().toString()))))
                        .andExpect(jsonPath("$.employee.id", is(1)));
    }

    @Test
    void getAllShiftByEmployeeFromToDate() {
    }

    @Test
    void addShiftToDatabase() {

        //mockMvc.perform(post("/api/v1/shift/"))

    }

    @Test
    void deleteShiftFromDatabase() {
    }

    @Test
    void changeEmployee() {
    }

    @Test
    void changeWorkedTime() {
    }

    @Test
    void changeActualWorkTime() {
    }

    @Test
    void countAllShift() {
    }
}