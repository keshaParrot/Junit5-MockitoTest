package ivan.denysiuk.learntest.Controller;

import ivan.denysiuk.learntest.Services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domain.dto.ShiftDto;
import ivan.denysiuk.learntest.domain.entity.Shift;
import ivan.denysiuk.learntest.domain.mapper.ShiftMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ShiftController {
    public final String SHIFT_PATH = "/api/v1/shift";
    public final String SHIFT_PATH_ID = SHIFT_PATH + "/{id}";
    private final ShiftService shiftService;

    @PostMapping(SHIFT_PATH+"/create")
    public ResponseEntity<ShiftDto> addShiftsToDatabase(@Valid @RequestBody ShiftDto shiftDto,
                                                        BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(shiftDto);
        }
        Shift createdShift = shiftService.saveShift(shiftDto);
        return new ResponseEntity<>(getDtoFromShift(createdShift), HttpStatus.CREATED);
    }
    @GetMapping(SHIFT_PATH_ID)
    public ResponseEntity<ShiftDto> getShiftById(@PathVariable("id") Long id){
        ShiftDto shiftDto = getDtoFromShift(shiftService.getShiftById(id));

        if (shiftDto==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shiftDto);
    }

    @GetMapping(SHIFT_PATH)
    public ResponseEntity<List<ShiftDto>> getAllShiftByEmployee(@RequestAttribute(value = "Emp", required = true) Optional<Long> employeeId,
                                                                @RequestAttribute(value = "From",required = false) Optional<YearMonth> monthFrom,
                                                                @RequestAttribute(value = "To",required = false) Optional<YearMonth> monthTo){
        List<ShiftDto> allEmployeeShift;

        if(monthFrom.isPresent() || monthTo.isPresent()){
            allEmployeeShift = shiftService.getAllShiftByEmployeeFromToDate(employeeId.get(),
                            monthFrom.orElseGet(() -> YearMonth.of(2020, 2)),
                            monthTo.orElseGet(YearMonth::now))
                    .stream()
                    .map(this::getDtoFromShift)
                    .toList();
        }
        else {
            allEmployeeShift = shiftService.getAllShiftByEmployee(employeeId.get())
                    .stream()
                    .map(this::getDtoFromShift)
                    .toList();
        }
        return ResponseEntity.ok(allEmployeeShift);
    }

    @DeleteMapping(SHIFT_PATH_ID+"/delete")
    public ResponseEntity deleteShiftFromDatabase(@PathVariable("id") Long id){
        boolean deleted = shiftService.deleteShift(id);
        if(deleted) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping(SHIFT_PATH_ID+"/update")
    public ResponseEntity<ShiftDto> updateShift(@PathVariable("id") Long id,
                                                @Valid @RequestBody ShiftDto shiftDto,
                                                BindingResult bindingResult){
        Shift updatedShift = shiftService.updateShift(id,shiftDto);

        if (bindingResult.hasErrors()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Errors", bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage() + "\n").toList().toString());

            return ResponseEntity.badRequest().headers(headers).body(shiftDto);
        }
        if(updatedShift != null) {
            return new ResponseEntity<>(getDtoFromShift(updatedShift), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping(SHIFT_PATH_ID+"/update")
    public ResponseEntity<ShiftDto> patchShift(@PathVariable("id") Long id,
                                               @Valid @RequestBody ShiftDto shiftDto,
                                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<FieldError> errors = bindingResult.getFieldErrors().stream()
                    .filter(fieldError -> "startTime".equals(fieldError.getField()) || "endTime".equals(fieldError.getField()))
                    .toList();

            if (!errors.isEmpty()){
                HttpHeaders headers = new HttpHeaders();
                headers.add("Errors", errors.stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("\n")));
                return ResponseEntity.badRequest().headers(headers).body(shiftDto);
            }
        }

        Shift updatedShift = shiftService.patchShift(id,shiftDto);

        if(updatedShift != null) {
            return new ResponseEntity<>(getDtoFromShift(updatedShift), HttpStatus.ACCEPTED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    Shift getShiftFromDto(ShiftDto shiftDto){
        return ShiftMapper.INSTANCE.DtoToShift(shiftDto);
    }
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift);
    }

}
