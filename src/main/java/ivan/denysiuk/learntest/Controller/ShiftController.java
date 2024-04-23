package ivan.denysiuk.learntest.Controller;

import ivan.denysiuk.learntest.Services.interfaces.EmployeeService;
import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/shift/")
public class ShiftController {
    private final ShiftService shiftService;
/*
    public ResponseEntity getShiftById(){

    }
    public ResponseEntity getAllShift(){

    }
    public ResponseEntity getAllShiftByEmployee(){

    }
    public ResponseEntity getAllShiftByEmployeeFromToDate(){

    }
    public ResponseEntity addShiftToDatabase(){

    }
    public ResponseEntity deleteShiftFromDatabase(){

    }
    public ResponseEntity changeEmployee(){

    }
    public ResponseEntity changeWorkedTime(){

    }
    public ResponseEntity changeActualWorkTime(){

    }
    public ResponseEntity countAllShift(){

    }
 */
}
