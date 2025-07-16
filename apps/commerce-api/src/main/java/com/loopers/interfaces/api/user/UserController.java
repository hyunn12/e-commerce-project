package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
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
    public ApiResponse<UserDto.UserResponse> getUserInfo(
            @RequestHeader("X-USER-ID") String userId
    ) {
        return ApiResponse.success(UserDto.UserResponse.from(userFacade.getUserInfo(userId)));
    }

}
