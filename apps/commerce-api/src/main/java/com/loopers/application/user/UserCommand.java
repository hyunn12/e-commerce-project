package com.loopers.application.user;

import com.loopers.domain.user.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

    @Getter
    @Builder
    public static class Join {
        private String loginId;
        private String email;
        private String gender;
        private String birth;

        public User toDomain() {
            return new User(
                    LoginId.of(loginId),
                    Email.of(email),
                    Gender.fromValue(gender),
                    Birth.of(birth)
            );
        }
    }
}
