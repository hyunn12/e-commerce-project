package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCommand;
import com.loopers.application.user.UserInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.loopers.support.utils.Validation.Pattern.*;
import static com.loopers.support.utils.Validation.Message.*;

public class UserDto {

    public record JoinRequest(
            @NotNull @Pattern(regexp = PATTERN_LOGIN_ID, message = MESSAGE_USER_LOGIN_ID)
            String loginId,
            @NotNull @Pattern(regexp = PATTERN_EMAIL, message = MESSAGE_USER_EMAIL)
            String email,
            @NotNull @Pattern(regexp = PATTERN_GENDER, message = MESSAGE_USER_GENDER)
            String gender,
            @NotNull @Pattern(regexp = PATTERN_BIRTH, message = MESSAGE_USER_BIRTH)
            String birth
    ) {
        public UserCommand.Join toCommand() {
            return UserCommand.Join.builder()
                    .loginId(loginId)
                    .email(email)
                    .gender(gender)
                    .birth(birth)
                    .build();
        }
    }

    public record UserResponse(Long id, String loginId, String email, String gender, String birth) {
        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                    info.getId(),
                    info.getLoginId(),
                    info.getEmail(),
                    info.getGender(),
                    info.getBirth()
            );
        }
    }
}
