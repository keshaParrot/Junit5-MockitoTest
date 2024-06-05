package ivan.denysiuk.learntest.repositories;

import ivan.denysiuk.learntest.domains.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    @Query("select e from Employee e where e.id = :id")
    Employee getEmployeeById(Long id);


    /**
     * Searchinf an employees by user-defined params
     *
     * @param pesel
     * @param fullName
     * @param specialization
     * @return
     */
    @Query("SELECT e FROM Employee e WHERE " +
            "(:pesel IS NULL OR e.PESEL = :pesel) AND " +
            "(:fullName IS NULL OR LOWER(CONCAT(e.firstName, ' ', e.lastName)) = lower(:fullName)) AND " +
            "(:specialization IS NULL OR e.specialization = :specialization)")
    List<Employee> findByParams(@Param("pesel") String pesel,
                                @Param("fullName") String fullName,
                                @Param("specialization") String specialization);
}
