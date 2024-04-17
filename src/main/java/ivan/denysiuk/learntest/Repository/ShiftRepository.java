package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for working with Shifts table on database.
 */
public interface ShiftRepository extends JpaRepository<Shift,Long> {

    /**
     * Searching shift by id
     *
     * @param id of the searched shift
     * @return the searched shift
     */
    @Query("SELECT s from Shift s where s.id = :id")
    Shift getShiftById(Long id);

    /**
     * Searching the shift of an employee by their ID.
     *
     * @param employeeId the ID of the employee whose shift is being retrieved
     * @return the searched shift
     */
    @Query("SELECT s from Shift s where s.employee.id = :employeeId")
    Shift getShiftByEmployeeId(Long employeeId);

}
