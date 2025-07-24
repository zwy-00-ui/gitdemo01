package com.example.vacation.service;

import com.example.vacation.entity.Vacation;
import com.example.vacation.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VacationService {
    private static final String FILE_PATH = "src/main/resources/data/vacations.json";

    public List<Vacation> getAllVacations() throws IOException {
        return new ArrayList<>(FileUtil.readFromFile(FILE_PATH, new TypeReference<List<Vacation>>() {}));
    }

    public List<Vacation> getVacationsByEmployeeId(int employeeId) throws IOException {
        List<Vacation> vacations = getAllVacations();
        return new ArrayList<>(vacations.stream()
                .filter(v -> v.getEmployeeId() == employeeId)
                .collect(Collectors.toList()));
    }

    public Vacation getVacationById(int id) throws IOException {
        List<Vacation> vacations = getAllVacations();
        Optional<Vacation> vacation = vacations.stream()
                .filter(v -> v.getId() == id)
                .findFirst();
        return vacation.orElse(null);
    }

    public Vacation addVacation(Vacation vacation) throws IOException {
        List<Vacation> vacations = getAllVacations();
        // 生成唯一ID
        int maxId = vacations.stream().mapToInt(Vacation::getId).max().orElse(0);
        vacation.setId(maxId + 1);
        vacation.setUseDays(0);
        vacation.setStatus("active");
        vacations.add(vacation);
        FileUtil.writeToFile(FILE_PATH, vacations);
        return vacation;
    }

    public boolean updateVacationUseDays(int vacationId, int days) throws IOException {
        List<Vacation> vacations = getAllVacations();
        for (Vacation vacation : vacations) {
            if (vacation.getId() == vacationId) {
                int newUseDays = vacation.getUseDays() + days;
                if (newUseDays > vacation.getTotalDays()) {
                    return false;
                }
                vacation.setUseDays(newUseDays);
                FileUtil.writeToFile(FILE_PATH, vacations);
                return true;
            }
        }
        return false;
    }

    public List<Vacation> getVacationsByEmployeeIdAndType(int employeeId, String type) throws IOException {
        List<Vacation> vacations = getAllVacations();
        return new ArrayList<>(vacations.stream()
                .filter(v -> v.getEmployeeId() == employeeId && v.getType().equals(type))
                .collect(Collectors.toList()));
    }
}