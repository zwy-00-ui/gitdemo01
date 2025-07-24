package com.example.vacation.service;

import com.example.vacation.entity.Vacation;
import com.example.vacation.entity.VacationRequest;
import com.example.vacation.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VacationRequestService {
    private static final String FILE_PATH = "src/main/resources/data/vacationRequests.json";

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VacationService vacationService;

    public List<VacationRequest> getAllVacationRequests() throws IOException {
        return new ArrayList<>(FileUtil.readFromFile(FILE_PATH, new TypeReference<List<VacationRequest>>() {}));
    }

    public List<VacationRequest> getVacationRequestsByEmployeeId(int employeeId) throws IOException {
        List<VacationRequest> requests = getAllVacationRequests();
        return new ArrayList<>(requests.stream()
                .filter(r -> r.getEmployeeId() == employeeId)
                .collect(Collectors.toList()));
    }

    public VacationRequest getVacationRequestById(int id) throws IOException {
        List<VacationRequest> requests = getAllVacationRequests();
        Optional<VacationRequest> request = requests.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
        return request.orElse(null);
    }

    public VacationRequest submitVacationRequest(VacationRequest request) throws IOException {
        List<VacationRequest> requests = getAllVacationRequests();
        // 生成唯一ID
        int maxId = requests.stream().mapToInt(VacationRequest::getId).max().orElse(0);
        request.setId(maxId + 1);
        request.setStatus("pending");
        requests.add(request);
        FileUtil.writeToFile(FILE_PATH, requests);
        return request;
    }

    public boolean processVacationRequest(int requestId, boolean approved) throws IOException {
        List<VacationRequest> requests = getAllVacationRequests();
        for (int i = 0; i < requests.size(); i++) {
            VacationRequest request = requests.get(i);
            if (request.getId() == requestId && "pending".equals(request.getStatus())) {
                if (approved) {
                    request.setStatus("approved");
                    // 计算假期天数差 (假设startDate和endDate格式为yyyy-MM-dd)
                    long days = calculateDaysBetween(request.getStartDate(), request.getEndDate());
                    if (days <= 0) {
                        return false;
                    }

                    // 获取员工对应的假期类型记录
                    List<Vacation> vacations = vacationService.getVacationsByEmployeeIdAndType(
                            request.getEmployeeId(), request.getType());
                    if (vacations.isEmpty()) {
                        return false;
                    }
                    Vacation vacation = vacations.get(0);

                    // 更新假期使用天数
                    boolean vacationUpdated = vacationService.updateVacationUseDays(vacation.getId(), (int) days);
                    // 扣除员工总假期天数
                    boolean employeeUpdated = employeeService.deductEmployeeVacationDays(
                            request.getEmployeeId(), (int) days);

                    if (vacationUpdated && employeeUpdated) {
                        requests.set(i, request);
                        FileUtil.writeToFile(FILE_PATH, requests);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    request.setStatus("rejected");
                    requests.set(i, request);
                    FileUtil.writeToFile(FILE_PATH, requests);
                    return true;
                }
            }
        }
        return false;
    }

    private long calculateDaysBetween(String startDate, String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        } catch (Exception e) {
            return 0;
        }
    }
}