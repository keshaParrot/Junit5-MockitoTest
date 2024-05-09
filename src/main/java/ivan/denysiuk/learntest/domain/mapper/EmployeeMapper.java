package ivan.denysiuk.learntest.domain.mapper;

import ivan.denysiuk.learntest.domain.dto.EmployeeDto;
import ivan.denysiuk.learntest.domain.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDto employeeToDto(Employee employee);
    Employee dtoToEmployee(EmployeeDto employeeDto);

}
