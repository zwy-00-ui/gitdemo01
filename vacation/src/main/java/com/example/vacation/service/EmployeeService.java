package com.example.vacation.service;

import com.example.vacation.entity.Employee;
import com.example.vacation.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private static final String FILE_PATH = "src/main/resources/data/employees.json";

    public List<Employee> getAllEmployees() throws IOException {
        return new ArrayList<>(FileUtil.readFromFile(FILE_PATH, new TypeReference<List<Employee>>() {}));
    }

    public Employee getEmployeeById(int id) throws IOException {
        List<Employee> employees = getAllEmployees();
        Optional<Employee> employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        return employee.orElse(null);
    }

    public Employee addEmployee(Employee employee) throws IOException {
        List<Employee> employees = getAllEmployees();
        // 设置初始假期天数为0
        employee.setTotalVacationDays(0);
        employee.setVacationDaysLeft(0);
        // 生成唯一ID
        int maxId = employees.stream().mapToInt(Employee::getId).max().orElse(0);
        employee.setId(maxId + 1);
        employees.add(employee);
        FileUtil.writeToFile(FILE_PATH, employees);
        return employee;
    }

    public Employee updateEmployee(Employee updatedEmployee) throws IOException {
        List<Employee> employees = getAllEmployees();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == updatedEmployee.getId()) {
                // 保留原有的假期天数
                updatedEmployee.setTotalVacationDays(employees.get(i).getTotalVacationDays());
                updatedEmployee.setVacationDaysLeft(employees.get(i).getVacationDaysLeft());
                employees.set(i, updatedEmployee);
                FileUtil.writeToFile(FILE_PATH, employees);
                return updatedEmployee;
            }
        }
        return null;
    }

    public boolean deleteEmployee(int id) throws IOException {
        List<Employee> employees = getAllEmployees();
        List<Employee> updatedEmployees = employees.stream()
                .filter(e -> e.getId() != id)
                .collect(Collectors.toList());
        if (updatedEmployees.size() != employees.size()) {
            FileUtil.writeToFile(FILE_PATH, updatedEmployees);
            return true;
        }
        return false;
    }

    // 更新员工总假期天数
    public void updateEmployeeVacationDays(int employeeId, int days) throws IOException {
        List<Employee> employees = getAllEmployees();
        for (Employee employee : employees) {
            if (employee.getId() == employeeId) {
                employee.setTotalVacationDays(employee.getTotalVacationDays() + days);
                employee.setVacationDaysLeft(employee.getVacationDaysLeft() + days);
                break;
            }
        }
        FileUtil.writeToFile(FILE_PATH, employees);
    }

    // 扣除员工假期天数
    public boolean deductEmployeeVacationDays(int employeeId, int days) throws IOException {
        List<Employee> employees = getAllEmployees();
        for (Employee employee : employees) {
            if (employee.getId() == employeeId) {
                if (employee.getVacationDaysLeft() >= days) {
                    employee.setVacationDaysLeft(employee.getVacationDaysLeft() - days);
                    FileUtil.writeToFile(FILE_PATH, employees);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}