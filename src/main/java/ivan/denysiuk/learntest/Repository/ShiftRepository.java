package ivan.denysiuk.learntest.Repository;

import ivan.denysiuk.learntest.domain.entity.Employee;
import ivan.denysiuk.learntest.domain.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShiftRepository extends JpaRepository<Shift,Long> {

    @Query("SELECT s from Shift s where s.id = :id")
    Shift getShiftById(Long id);
    @Query("SELECT s from Shift s where s.employee.id = :employeeId")
    Shift getShiftByEmployeeId(Long employeeId);

}
