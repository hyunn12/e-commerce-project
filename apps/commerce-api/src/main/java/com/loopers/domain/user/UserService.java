package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel save(UserModel userModel) {
        // 가입 확인
        if (userRepository.existsByUserId(userModel.getUserId())) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 존재하는 아이디 입니다.");
        }

        return userRepository.save(userModel);
    }

    public UserModel findByUserId(String userId) {
        return userRepository.getUserByUserId(userId);
    }
}
