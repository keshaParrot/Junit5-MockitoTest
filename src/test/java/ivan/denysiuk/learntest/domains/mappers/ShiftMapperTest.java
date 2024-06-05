package ivan.denysiuk.learntest.domains.mappers;

import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import ivan.denysiuk.learntest.domains.entities.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShiftMapperTest {

    Shift shift;
    ShiftDto shiftDto;
    Employee employee;
    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .build();
        shift = Shift.builder()
                .id(1L)
                .date(LocalDate.of(2022,2,2))
                .startTime("12:30")
                .endTime("13:30")
                .station("engineer")
                .actualStartTime("12:31")
                .actualEndTime("13:30")
                .employee(employee)
                .build();

        shiftDto = ShiftDto.builder()
                .id(1L)
                .date(LocalDate.of(2022,2,2))
                .startTime("12:30")
                .endTime("13:30")
                .station("engineer")
                .actualStartTime("12:31")
                .actualEndTime("13:30")
                .employeeId(1L)
                .build();
    }
    @Test
    void dtoToShift() {
        Shift convertedShift = getShiftFromDto(shiftDto);
        convertedShift.setEmployee(employee);
        assertEquals(shift,convertedShift);
    }

    @Test
    void shiftToDto() {
        assertEquals(shiftDto,getDtoFromShift(shift));
    }

    Shift getShiftFromDto(ShiftDto shift){
        return ShiftMapper.INSTANCE.DtoToShift(shift);
    }
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift);
    }

}