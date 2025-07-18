package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.loopers.support.utils.Validation.Pattern.*;
import static com.loopers.support.utils.Validation.Message.*;

public class UserDto {

    public record JoinRequest(
            @NotNull @Pattern(regexp = PATTERN_USER_ID, message = MESSAGE_USER_ID)
            String userId,
            @NotNull @Pattern(regexp = PATTERN_EMAIL, message = MESSAGE_EMAIL)
            String email,
            @NotNull @Pattern(regexp = PATTERN_GENDER, message = MESSAGE_GENDER)
            String gender,
            @NotNull @Pattern(regexp = PATTERN_BIRTH, message = MESSAGE_BIRTH)
            String birth
    ) {
        public UserInfo toInfo() {
            return new UserInfo(
                    null,
                    userId,
                    email,
                    gender,
                    birth
            );
        }
    }

    public record UserResponse(Long id, String userId, String email, String gender, String birth) {
        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                    info.id(),
                    info.userId(),
                    info.email(),
                    info.gender(),
                    info.birth()
            );
        }
    }
}
