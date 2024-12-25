package com.aivle.mini7.controller;

import com.aivle.mini7.client.api.FastApiClient;
import com.aivle.mini7.client.dto.HospitalResponse;
import com.aivle.mini7.service.LogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors; // 올바른 임포트 추가

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final FastApiClient fastApiClient;
    private final LogService logService;
    @GetMapping("/") // 새로운 main 페이지 매핑
    public String mainPage() {
        return "main"; // main.html 반환
    }

    @GetMapping("/recommend_hospital")
    public String recommendHospital(
            @RequestParam("request") String request,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("address") String address,
            @RequestParam("number_of_hospitals") int number_of_hospitals,
            Model model) {

        List<HospitalResponse> hospitalList = fastApiClient.getHospital(request, latitude, longitude, address, number_of_hospitals);
        log.info("FastApiClient returned {} hospitals: {}", hospitalList.size(), hospitalList);

        // 병원 리스트 제한
        List<HospitalResponse> limitedHospitals = hospitalList.stream()
                .limit(number_of_hospitals)
                .collect(Collectors.toList()); // 오류 없는 정적 메서드 호출

        model.addAttribute("hospitalList", limitedHospitals);

        // 로그 저장
        logService.saveLog(hospitalList, request, latitude, longitude, 2);

        return "main";
    }
}
