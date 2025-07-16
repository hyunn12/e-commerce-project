package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    @PostMapping("/charge")
    public ApiResponse<Object> charge() {
        return ApiResponse.success(
                new PointDto.PointResponse("test123", 100000)
        );
    }

    @GetMapping
    public ApiResponse<Object> getPoint() {
        return ApiResponse.success(
                new PointDto.PointResponse("test123", 100000)
        );
    }

}
