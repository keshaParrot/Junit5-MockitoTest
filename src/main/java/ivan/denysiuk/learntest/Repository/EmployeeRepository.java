package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
