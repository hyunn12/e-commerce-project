package com.loopers.domain.user;

import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
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
    // orm --
    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 회원_가입_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 정상적으로_회원_가입이_수행된다 {

            @DisplayName("유효한 값이 주어진다면")
            @Test
            void 유효한_값이_주어진다면() {
                // arrange
                User user = User.saveBuilder()
                        .loginId(LoginId.of("test123"))
                        .email(Email.of("test@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();

                // act
                userService.save(user);

                // assert
                verify(userJpaRepository, times(1)).save(user);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 회원_가입에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("가입된 아이디가 주어진다면")
            @Test
            void 가입된_아이디가_주어진다면() {
                // arrange
                String duplicatedId = "test123";

                User user1 = User.saveBuilder()
                        .loginId(LoginId.of(duplicatedId))
                        .email(Email.of("test1@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();
                userService.save(user1);

                User user2 = User.saveBuilder()
                        .loginId(LoginId.of(duplicatedId))
                        .email(Email.of("test2@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();

                // act
                assertThrows(CoreException.class, () -> userService.save(user2));

                // assert
                verify(userJpaRepository, times(2)).existsByLoginId_Value(duplicatedId);
                verify(userJpaRepository, times(1)).save(user1);
                verify(userJpaRepository, times(0)).save(user2);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 내_정보_조회_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 회원정보를_반환한다 {

            @DisplayName("주어진 loginId의 회원이 존재하는 회원이라면")
            @Test
            void 주어진_loginId의_회원이_존재하는_회원이라면() {
                // arrange
                User user = User.saveBuilder()
                        .loginId(LoginId.of("test123"))
                        .email(Email.of("test@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();
                userService.save(user);

                // act
                userService.getDetail(user.getId());

                // assert
                Optional<User> saved = userJpaRepository.findById(user.getId());
                assertThat(saved.get().getEmail()).isEqualTo(user.getEmail());
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class Null을_반환한다 {

        @DisplayName("주어진 loginId의 회원이 존재하지 않는 회원이라면")
        @Test
        void 주어진_loginId의_회원이_존재하지_않는_회원이라면() {
            // arrange
            Long userId = 1L;

            // act
            User user = userService.getDetail(userId);

            // assert
            assertThat(user).isNull();
        }
    }
}
