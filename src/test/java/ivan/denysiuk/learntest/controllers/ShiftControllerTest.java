package ivan.denysiuk.learntest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.denysiuk.learntest.services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import ivan.denysiuk.learntest.domains.entities.Shift;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @Autowired
    ObjectMapper objectMapper;
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
                .station("Station A")
                .startTime("22:30")
                .endTime("06:00")
                .date(LocalDate.of(2024,5,4))
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
    void getAllShiftByEmployeeFromToDate() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(get("/api/v1/shift/searchbyemployee/"+ employeeId)
                        .param("from", "3")
                        .param("To", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee.id",is(employeeId)))
                .andExpect(jsonPath("$[0].startTime",is("22:30")));

    }

    @Test
    void addShiftToDatabase() throws Exception {
        given(shiftService.saveShift(any(ShiftDto.class))).willReturn(shift);

        mockMvc.perform(post("/api/v1/shift/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shift)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteShiftFromDatabase() throws Exception {
        when(shiftService.deleteShift(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/shift/"+ shift.getId() +"/delete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(shiftService).deleteShift(any());
    }

    @Test
    void patchShift() throws Exception {
        mockMvc.perform(patch("/api/v1/employee/"+shift.getId()+"/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)));

        verify(shiftService).updateShift(anyLong(),any(ShiftDto.class));
    }

    @Test
    void updateShift() throws Exception {
        mockMvc.perform(put("/api/v1/employee/"+shift.getId()+"/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)));

        verify(shiftService).updateShift(anyLong(),any(ShiftDto.class));
    }
}