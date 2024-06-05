package ivan.denysiuk.learntest.domains.mappers;

import ivan.denysiuk.learntest.domains.dtos.EmployeeDto;
import ivan.denysiuk.learntest.domains.entities.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDto employeeToDto(Employee employee);
    Employee dtoToEmployee(EmployeeDto employeeDto);

}
