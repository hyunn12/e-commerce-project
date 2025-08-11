package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@Tag(name = "User V1 API", description = "회원 관련 API")
public interface UserV1ApiSpec {

    @PostMapping
    @Operation(summary = "회원 가입", description = "새로운 회원 생성")
    ApiResponse<UserV1Dto.UserResponse> joinUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "기본 예시",
                                    value = """
                                        {
                                          "loginId": "test123",
                                          "email": "test@test.com",
                                          "gender": "F",
                                          "birth": "2000-01-01"
                                        }
                                    """
                            )
                    )
            )
            @RequestBody @Valid UserV1Dto.JoinRequest request
    );

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "회원 ID 로 회원 상세 정보 조회")
    ApiResponse<UserV1Dto.UserResponse> getUserInfo(
            @Parameter(description = "사용자 식별 USER_ID 헤더", example = "1")
            @RequestHeader(USER_USER_ID_HEADER) Long userId
    );
}
