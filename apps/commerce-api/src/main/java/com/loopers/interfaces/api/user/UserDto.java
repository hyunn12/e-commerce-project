package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.validation.constraints.NotNull;

public class UserDto {

    public record JoinRequest(
            @NotNull
            String userId,
            @NotNull
            String email,
            @NotNull
            String gender,
            @NotNull
            String birth
    ) {
        private static final String PATTERN_USER_ID = "^[a-zA-Z0-9]{1,10}$";
        private static final String PATTERN_EMAIL = "^[a-z]+@[a-z]+\\.[a-z]{2,}$";
        private static final String PATTERN_BIRTH = "^\\d{4}-\\d{2}-\\d{2}$";

        public void validate() {
            if (userId == null || !userId.matches(PATTERN_USER_ID)) {
                throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 영문 및 숫자 10자 이내로만 작성해야 합니다.");
            }

            if (email == null || !email.matches(PATTERN_EMAIL)) {
                throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 xx@yy.zz 형식으로 작성해야 합니다.");
            }

            if (gender == null || !(gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F"))) {
                throw new CoreException(ErrorType.BAD_REQUEST, "성별 형식이 잘못되었습니다.");
            }

            if (birth == null || !birth.matches(PATTERN_BIRTH)) {
                throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
            }
        }

        public UserInfo toInfo() {
            validate();

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
