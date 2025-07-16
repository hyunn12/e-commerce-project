package com.loopers.application.user;

import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserInfo join(UserInfo info) {
        return UserInfo.from(userService.save(info.toModel()));
    }

    public UserInfo getUserInfo(String userId) {
        return UserInfo.from(userService.findByUserId(userId));
    }

}
