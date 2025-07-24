package com.example.vacation.entity;

import lombok.Data;

@Data
public class Employee {
    private int id;
    private String name;
    private int level;
    private int totalVacationDays;
    private int vacationDaysLeft;

}