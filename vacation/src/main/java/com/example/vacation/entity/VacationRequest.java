package com.example.vacation.entity;

import lombok.Data;

@Data
public class VacationRequest {
    private int id;
    private int employeeId;
    private String type;
    private String startDate;
    private String endDate;
    private String status;
}