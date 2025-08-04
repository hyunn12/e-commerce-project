package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.loopers.support.utils.Validation.Message.MESSAGE_USER_LOGIN_ID_EXIST;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        // 가입 확인
        if (userRepository.existsByLoginId(user.getLoginId())) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_USER_LOGIN_ID_EXIST);
        }

        return userRepository.save(user);
    }

    public User getDetail(Long userId) {
        return userRepository.getUserById(userId);
    }
}
