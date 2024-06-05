package ivan.denysiuk.learntest.controllers;

import ivan.denysiuk.learntest.services.interfaces.ShiftService;
import ivan.denysiuk.learntest.domains.dtos.ShiftDto;
import ivan.denysiuk.learntest.domains.entities.Shift;
import ivan.denysiuk.learntest.domains.mappers.ShiftMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing shifts.
 */
@RestController
@AllArgsConstructor
public class ShiftController {
    public final String SHIFT_PATH = "/api/v1/shift";
    public final String SHIFT_PATH_ID = SHIFT_PATH + "/{id}";
    private final ShiftService shiftService;

    /**
     * Adds a new shift to the database.
     *
     * @param shiftDto the ShiftDto object containing the details of the shift to add
     * @param bindingResult the result of the validation of the shift details
     * @return a ResponseEntity containing the created ShiftDto object
     */
    @PostMapping(SHIFT_PATH+"/create")
    public ResponseEntity<ShiftDto> addShiftsToDatabase(@Valid @RequestBody ShiftDto shiftDto,
                                                        BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            return buildErrorResponse(errorMessage,HttpStatus.BAD_REQUEST,shiftDto);
        }
        Shift createdShift = shiftService.saveShift(shiftDto);
        return new ResponseEntity<>(getDtoFromShift(createdShift), HttpStatus.CREATED);
    }
    /**
     * Retrieves a shift by its ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ResponseEntity containing the ShiftDto object
     */
    @GetMapping(SHIFT_PATH_ID)
    public ResponseEntity<ShiftDto> getShiftById(@PathVariable("id") Long id){
        ShiftDto shiftDto = getDtoFromShift(shiftService.getShiftById(id));

        if (shiftDto==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shiftDto);
    }
    /**
     * Retrieves all shifts for an employee, optionally filtered by date range.
     *
     * @param employeeId the ID of the employee
     * @param monthFrom the start of the date range (optional)
     * @param monthTo the end of the date range (optional)
     * @return a ResponseEntity containing a list of ShiftDto objects
     */
    @GetMapping(SHIFT_PATH)
    public ResponseEntity<List<ShiftDto>> getAllShiftByEmployee(@RequestParam(value = "emp") Optional<Long> employeeId,
                                                                @RequestParam(value = "from",required = false) Optional<YearMonth> monthFrom,
                                                                @RequestParam(value = "to",required = false) Optional<YearMonth> monthTo){
        if (employeeId.isEmpty()) {
            return buildErrorResponse("Variable employee cannot be null", HttpStatus.BAD_REQUEST, null);
        }

        List<ShiftDto> allEmployeeShift;
        Long empId = employeeId.get();

        if (monthFrom.isPresent() || monthTo.isPresent()) {
            YearMonth from = monthFrom.orElse(YearMonth.of(2020, 2));
            YearMonth to = monthTo.orElse(YearMonth.now());
            allEmployeeShift = shiftService.getAllShiftByEmployeeFromToDate(empId, from, to)
                    .stream()
                    .map(this::getDtoFromShift)
                    .toList();
        } else {
            allEmployeeShift = shiftService.getAllShiftByEmployee(empId)
                    .stream()
                    .map(this::getDtoFromShift)
                    .toList();
        }
        if (allEmployeeShift.isEmpty()){
            return buildErrorResponse("employee dont have any shift with this parameters", HttpStatus.NOT_FOUND, null);
        }
        return ResponseEntity.ok(allEmployeeShift);
    }
    /**
     * Deletes a shift from the database by its ID.
     *
     * @param id the ID of the shift to delete
     * @return a ResponseEntity indicating the result of the operation
     */
    @DeleteMapping(SHIFT_PATH_ID+"/delete")
    public ResponseEntity deleteShiftFromDatabase(@PathVariable("id") Long id){
        boolean deleted = shiftService.deleteShift(id);
        if(deleted) {
            return new ResponseEntity(HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    /**
     * Updates an existing shift by its ID.
     *
     * @param id the ID of the shift to update
     * @param shiftDto the ShiftDto object containing the updated details of the shift
     * @param bindingResult the result of the validation of the shift details
     * @return a ResponseEntity containing the updated ShiftDto object
     */
    @PutMapping(SHIFT_PATH_ID+"/update")
    public ResponseEntity<ShiftDto> updateShift(@PathVariable("id") Long id,
                                                @Valid @RequestBody ShiftDto shiftDto,
                                                BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            return buildErrorResponse(errorMessage,HttpStatus.BAD_REQUEST,shiftDto);
        }

        Shift updatedShift = shiftService.updateShift(id,shiftDto);

        if(updatedShift != null) {
            return ResponseEntity.ok(getDtoFromShift(updatedShift));
        } else {
            return buildErrorResponse("Shift with this id not exist",HttpStatus.NOT_FOUND,shiftDto);
        }
    }
    /**
     * Partially updates an existing shift by its ID.
     *
     * @param id the ID of the shift to update
     * @param shiftDto the ShiftDto object containing the updated details of the shift
     * @param bindingResult the result of the validation of the shift details
     * @return a ResponseEntity containing the updated ShiftDto object
     */
    @PatchMapping(SHIFT_PATH_ID+"/update")
    public ResponseEntity<ShiftDto> patchShift(@PathVariable("id") Long id,
                                               @Valid @RequestBody ShiftDto shiftDto,
                                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<FieldError> errors = bindingResult.getFieldErrors().stream()
                    .filter(fieldError -> "startTime".equals(fieldError.getField()) || "endTime".equals(fieldError.getField()))
                    .toList();

            if (!errors.isEmpty()){
                String errorMessage = errors.stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("\n"));

                return buildErrorResponse(errorMessage,HttpStatus.BAD_REQUEST,shiftDto);
            }
        }

        Shift updatedShift = shiftService.patchShift(id,shiftDto);

        if(updatedShift != null) {
            return ResponseEntity.ok(getDtoFromShift(updatedShift));
        }
        else {
            return buildErrorResponse("Shift with this id not exist",HttpStatus.NOT_FOUND,shiftDto);
        }
    }
    /**
     * Builds an error response with the given error message, status, and body.
     *
     * @param errorMessage the error message to include in the response
     * @param status the HTTP status of the response
     * @param body the body of the response
     * @param <T> the type of the body
     * @return a ResponseEntity containing the error message and status
     */
    private <T> ResponseEntity<T> buildErrorResponse(String errorMessage, HttpStatus status, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Errors", errorMessage);
        if (body != null) {
            return new ResponseEntity<>(body, headers, status);
        }
        return new ResponseEntity<>(headers, status);
    }
    /**
     * Converts a ShiftDto object to a Shift entity.
     *
     * @param shiftDto the ShiftDto object to convert
     * @return the corresponding Shift entity
     */
    Shift getShiftFromDto(ShiftDto shiftDto){
        return ShiftMapper.INSTANCE.DtoToShift(shiftDto);
    }
    /**
     * Converts a Shift entity to a ShiftDto object.
     *
     * @param shift the Shift entity to convert
     * @return the corresponding ShiftDto object
     */
    ShiftDto getDtoFromShift(Shift shift){
        return ShiftMapper.INSTANCE.ShiftToDto(shift);
    }

}
