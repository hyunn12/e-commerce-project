package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping
    public ApiResponse<UserV1Dto.UserResponse> joinUser(
            @RequestBody @Valid UserV1Dto.JoinRequest request
    ) {
        return ApiResponse.success(UserV1Dto.UserResponse.from(userFacade.join(request.toCommand())));
    }

    @GetMapping("/me")
    public ApiResponse<UserV1Dto.UserResponse> getUserInfo(
            @RequestHeader(USER_USER_ID_HEADER) Long userId
    ) {
        return ApiResponse.success(UserV1Dto.UserResponse.from(userFacade.getDetail(userId)));
    }
}
