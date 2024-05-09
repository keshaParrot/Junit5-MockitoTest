package ivan.denysiuk.learntest.domain.mapper;

import ivan.denysiuk.learntest.domain.dto.ShiftDto;
import ivan.denysiuk.learntest.domain.entity.Shift;
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
