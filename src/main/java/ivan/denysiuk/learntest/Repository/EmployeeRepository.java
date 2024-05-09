package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for working with Shifts table on database.
 */
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    /**
     * Searching employee by id
     *
     * @param id of the searched employee
     * @return the searched employee
     */
    @Query("select e from Employee e left join fetch e.workedShift where e.id = :id")
    Employee getEmployeeById(@Param("id") Long id);
    /**
     * Searching employee by full name
     *
     * @param fullName of the searched employee
     * @return the searched employee
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(CONCAT(e.firstName, ' ', e.lastName)) = lower(:fullName)")
    Employee getEmployeeByFullName(String fullName);
    /**
     * Searching employee by PESEL
     *
     * @param pesel of the searched employee
     * @return the searched employee
     */
    @Query("SELECT e FROM Employee e WHERE e.PESEL = :pesel")
    Employee getEmployeeByPESEL(String pesel);
}
