package com.loopers.application.user;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.transaction.Transactional;
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
        UserModel user = userService.findByUserId(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        return UserInfo.from(user);
    }
}
