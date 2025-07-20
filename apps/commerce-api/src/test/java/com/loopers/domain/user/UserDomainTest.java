package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDomainTest {

    static final String validId = "test123";
    static final String validEmail = "test@test.com";
    static final String validGender = "M";
    static final String validBirth = "2000-01-01";

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class User_객체_생성_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 객체_생성에_성공한다 {

            @DisplayName("유효한 값이 주어진다면")
            @Test
            void 유효한_값이_주어진다면() {
                assertDoesNotThrow(() -> User.saveBuilder()
                        .userId(validId)
                        .email(validEmail)
                        .gender(validGender)
                        .birth(validBirth)
                        .build()
                );
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 객체_생성에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("형식에 맞지 않는 아이디라면")
            @Test
            void 형식에_맞지_않는_아이디라면() {
                CoreException exception = assertThrows(CoreException.class, () -> User.saveBuilder()
                                .userId("test1234567")
                                .email(validEmail)
                                .gender(validGender)
                                .birth(validBirth)
                                .build()
                );

                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("형식에 맞지 않는 이메일이라면")
            @Test
            void 형식에_맞지_않는_이메일이라면() {
                CoreException exception = assertThrows(CoreException.class, () -> User.saveBuilder()
                        .userId(validId)
                        .email("test")
                        .gender(validGender)
                        .birth(validBirth)
                        .build()
                );

                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("유효하지 않은 성별이라면")
            @Test
            void 유효하지_않은_성별이라면() {
                CoreException exception = assertThrows(CoreException.class, () -> User.saveBuilder()
                        .userId(validId)
                        .email(validEmail)
                        .gender("N")
                        .birth(validBirth)
                        .build()
                );

                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("형식에 맞지 않는 생년월일이라면")
            @Test
            void 형식에_맞지_않는_생년월일이라면() {
                CoreException exception = assertThrows(CoreException.class, () -> User.saveBuilder()
                        .userId(validId)
                        .email(validEmail)
                        .gender(validGender)
                        .birth("2020.01.01")
                        .build()
                );

                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }
    }
}
