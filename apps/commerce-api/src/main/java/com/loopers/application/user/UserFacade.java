package com.loopers.application.user;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.loopers.support.utils.Validation.Message.MESSAGE_USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;

    @Transactional
    public UserInfo join(UserCommand.Join command) {
        UserInfo joinInfo = UserInfo.from(userService.save(command.toDomain()));

        // 포인트 초기화
        pointService.save(new Point(joinInfo.getId(), 0));

        return joinInfo;
    }

    public UserInfo getDetail(Long userId) {
        User user = userService.getDetail(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_USER_NOT_FOUND);
        }
        return UserInfo.from(user);
    }
}
