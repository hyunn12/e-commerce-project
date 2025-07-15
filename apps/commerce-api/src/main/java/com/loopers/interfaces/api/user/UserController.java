package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping
    public ApiResponse<UserDto.UserResponse> joinUser(
            @RequestBody UserDto.JoinRequest request
    ) {
        return ApiResponse.success(UserDto.UserResponse.from(userFacade.join(request.toInfo())));
    }

    @GetMapping("/me")
    public ApiResponse<UserDto.UserResponse> getUserInfo(HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");

        return ApiResponse.success(UserDto.UserResponse.from(userFacade.getUserInfo(userId)));
    }

}
