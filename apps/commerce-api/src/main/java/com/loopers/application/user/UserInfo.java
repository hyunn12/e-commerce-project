package com.loopers.application.user;

import com.loopers.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    private Long id;
    private String loginId;
    private String email;
    private String gender;
    private String birth;

    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId().getValue(),
                user.getEmail().getValue(),
                user.getGender().getValue(),
                user.getBirth().getValue()
        );
    }
}
