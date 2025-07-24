package com.example.vacation.controller;

import com.example.vacation.entity.VacationRequest;
import com.example.vacation.service.VacationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vacation-requests")
public class VacationRequestController {
    @Autowired
    private VacationRequestService vacationRequestService;

    @GetMapping
    public ResponseEntity<List<VacationRequest>> getAllVacationRequests() {
        try {
            List<VacationRequest> requests = vacationRequestService.getAllVacationRequests();
            return ResponseEntity.ok(requests);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<VacationRequest>> getVacationRequestsByEmployeeId(@PathVariable int employeeId) {
        try {
            List<VacationRequest> requests = vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
            return ResponseEntity.ok(requests);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacationRequest> getVacationRequestById(@PathVariable int id) {
        try {
            VacationRequest request = vacationRequestService.getVacationRequestById(id);
            if (request != null) {
                return ResponseEntity.ok(request);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<VacationRequest> submitVacationRequest(@RequestBody VacationRequest request) {
        try {
            VacationRequest newRequest = vacationRequestService.submitVacationRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRequest);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<Void> processVacationRequest(@PathVariable int id, @RequestParam boolean approved) {
        try {
            boolean processed = vacationRequestService.processVacationRequest(id, approved);
            if (processed) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}