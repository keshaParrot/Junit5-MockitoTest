package ivan.denysiuk.learntest.domain.mapper;

import ivan.denysiuk.learntest.domain.dto.SalaryDto;
import ivan.denysiuk.learntest.domain.dto.ShiftDto;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShiftMapper {

    ShiftMapper INSTANCE = Mappers.getMapper(ShiftMapper.class);
    Shift DtoToShift(ShiftDto shiftDto);
    ShiftDto ShiftToDto(Shift shift);
}
