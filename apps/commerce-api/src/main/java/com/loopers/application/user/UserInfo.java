package com.loopers.application.user;

import com.loopers.domain.user.UserModel;

public record UserInfo(Long id, String userId, String email, String gender, String birth) {

    public static UserInfo from(UserModel model) {
        return new UserInfo(
                model.getId(),
                model.getUserId(),
                model.getEmail(),
                model.getGender(),
                model.getBirth().toString()
        );
    }
}
