package com.example.vacation.controller;

import com.example.vacation.entity.Vacation;
import com.example.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vacations")
public class VacationController {
    @Autowired
    private VacationService vacationService;

    @GetMapping
    public ResponseEntity<List<Vacation>> getAllVacations() {
        try {
            List<Vacation> vacations = vacationService.getAllVacations();
            return ResponseEntity.ok(vacations);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Vacation>> getVacationsByEmployeeId(@PathVariable int employeeId) {
        try {
            List<Vacation> vacations = vacationService.getVacationsByEmployeeId(employeeId);
            return ResponseEntity.ok(vacations);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacation> getVacationById(@PathVariable int id) {
        try {
            Vacation vacation = vacationService.getVacationById(id);
            if (vacation != null) {
                return ResponseEntity.ok(vacation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Vacation> addVacation(@RequestBody Vacation vacation) {
        try {
            Vacation newVacation = vacationService.addVacation(vacation);
            return ResponseEntity.status(HttpStatus.CREATED).body(newVacation);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}