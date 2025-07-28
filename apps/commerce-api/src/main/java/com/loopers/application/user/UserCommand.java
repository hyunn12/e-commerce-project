package com.loopers.application.user;

import com.loopers.domain.user.User;
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
                    loginId,
                    email,
                    gender,
                    birth
            );
        }
    }
}
