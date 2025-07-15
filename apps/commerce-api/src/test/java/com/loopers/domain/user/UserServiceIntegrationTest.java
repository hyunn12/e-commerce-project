package com.loopers.domain.user;

import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원 가입 시")
    @Nested
    class Join {

        @DisplayName("정상 데이터로 회원가입 시 저장됨")
        @Test
        void joinWithValidData() {
            // arrange
            UserModel user = new UserModel("test123", "test@test.com", "F", "2000-01-01");

            // act
            userService.save(user);

            // assert
            verify(userJpaRepository, times(1)).save(user);
        }

        @DisplayName("가입된 ID 인 경우 예외 발생 spy")
        @Test
        void joinWithDuplicatedId_bySpy() {
            // arrange
            String duplicatedId = "test123";

            UserModel user1 = new UserModel(duplicatedId, "test1@test.com", "F", "2000-01-01");
            userService.save(user1);

            UserModel user2 = new UserModel(duplicatedId, "test2@test.com", "F", "2000-01-01");

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userService.save(user2));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).isEqualTo("이미 존재하는 아이디 입니다.");

            verify(userJpaRepository, times(2)).existsByUserId(duplicatedId);
            verify(userJpaRepository, times(1)).save(user1);
            verify(userJpaRepository, times(0)).save(user2);
        }

        @DisplayName("가입된 ID 인 경우 예외 발생")
        @Test
        void joinWithDuplicatedId() {
            // arrange
            String duplicatedId = "test123";
            UserModel user1 = new UserModel(duplicatedId, "test1@test.com", "F", "2000-01-01");
            userService.save(user1);

            UserModel user2 = new UserModel(duplicatedId, "test2@test.com", "F", "2000-01-01");

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userService.save(user2));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).isEqualTo("이미 존재하는 아이디 입니다.");

            Optional<UserModel> saved = userJpaRepository.findByUserId(duplicatedId);
            assertThat(saved).isPresent();
            assertThat(saved.get().getEmail()).isEqualTo("test1@test.com");
        }

    }

    @DisplayName("내 정보 조회 시")
    @Nested
    class GetUser {

        @DisplayName("해당 아이디의 회원이 존재할 경우, 회원 정보가 반환됨")
        @Test
        void getUserWithValidUserId() {
            // arrange
            String userId = "test123";
            UserModel user = new UserModel(userId, "test1@test.com", "F", "2000-01-01");
            userService.save(user);

            // act
            userService.findByUserId(userId);

            // assert
            Optional<UserModel> saved = userJpaRepository.findByUserId(userId);
            assertThat(saved).isPresent();
            assertThat(saved.get().getEmail()).isEqualTo("test1@test.com");
        }

        @DisplayName("해당 아이디의 회원이 존재하지 않을 경우, null이 반환됨")
        @Test
        void getUserWithInvalidUserId_returnNull() {
            // arrange
            String userId = "test123";

            // act
            UserModel user = userService.findByUserId(userId);

            // assert
            assertThat(user).isNull();
        }
    }
}
