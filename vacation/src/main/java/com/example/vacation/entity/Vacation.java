package com.example.vacation.entity;
import lombok.Data;

@Data
public class Vacation {
    private int id;
    private int employeeId;
    private String type;
    private int totalDays;
    private int useDays;
    private String status;

    public Vacation() {
    }

    public Vacation(int id, int employeeId, String type, int totalDays, int useDays, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.totalDays = totalDays;
        this.useDays = useDays;
        this.status = status;
    }

}
