package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping
    public ApiResponse<UserDto.UserResponse> joinUser(
            @RequestBody UserDto.JoinRequest request
    ) {
        return ApiResponse.success(
                new UserDto.UserResponse(
                        1L,
                        request.userId(),
                        request.email(),
                        request.gender(),
                        request.birth()
                )
        );
    }

    @GetMapping("/me")
    public ApiResponse<UserDto.UserResponse> getUserInfo(
    ) {
        return ApiResponse.success(
                new UserDto.UserResponse(
                        1L,
                        "test user",
                        "test@test.com",
                        "F",
                        "2000-12-25"
                )
        );
    }

}
