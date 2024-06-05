package ivan.denysiuk.learntest.domains.mappers;

import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShiftMapper {

    ShiftMapper INSTANCE = Mappers.getMapper(ShiftMapper.class);
    Shift DtoToShift(ShiftDto shiftDto);
    @Mapping(source = "shift.employee.id", target = "employeeId")
    ShiftDto ShiftToDto(Shift shift);
}
