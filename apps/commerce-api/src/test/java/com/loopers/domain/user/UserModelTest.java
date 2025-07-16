package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    static final String validId = "test123";
    static final String validEmail = "test@test.com";
    static final String validGender = "M";
    static final String validBirth = "2000-01-01";

    @Nested
    @DisplayName("아이디 검증")
    class UserId {

        @Test
        @DisplayName("유효한 아이디인 경우 정상적으로 객체가 생성됨")
        void createUserByValidId() {
            assertDoesNotThrow(() -> UserModel.saveBuilder()
                    .userId(validId)
                    .email(validEmail)
                    .gender(validGender)
                    .birth(validBirth)
                    .build()
            );
        }

        @Test
        @DisplayName("유효하지 않은 아이디의 경우 예외 발생")
        void throwExceptionByInvalidId() {
            CoreException exception = assertThrows(CoreException.class, () -> UserModel.saveBuilder()
                    .userId("test1234567")
//                    .userId("test123!!")
                    .email(validEmail)
                    .gender(validGender)
                    .birth(validBirth)
                    .build()
            );

            assertThat(exception.getMessage()).isEqualTo("아이디는 영문 및 숫자 10자 이내로만 작성해야 합니다.");
        }
    }

    @Nested
    @DisplayName("이메일 검증")
    class Email {

        @Test
        @DisplayName("유효하지 않은 이메일의 경우 예외 발생")
        void throwExceptionByInvalidEmail() {
            CoreException exception = assertThrows(CoreException.class, () -> UserModel.saveBuilder()
                    .userId(validId)
                    .email("test")
                    .gender(validGender)
                    .birth(validBirth)
                    .build()
            );

            assertThat(exception.getMessage()).isEqualTo("이메일은 xx@yy.zz 형식으로 작성해야 합니다.");
        }
    }

    @Nested
    @DisplayName("성별 검증")
    class Gender {

        @Test
        @DisplayName("유효하지 않은 성별의 경우 예외 발생")
        void throwExceptionByInvalidGender() {
            CoreException exception = assertThrows(CoreException.class, () -> UserModel.saveBuilder()
                    .userId(validId)
                    .email(validEmail)
                    .gender("N")
                    .birth(validBirth)
                    .build()
            );

            assertThat(exception.getMessage()).isEqualTo("성별 형식이 잘못되었습니다.");
        }
    }

    @Nested
    @DisplayName("생년월일 검증")
    class Birth {

        @Test
        @DisplayName("유효하지 않은 생일의 경우 예외 발생")
        void throwExceptionByInvalidBirth() {
            CoreException exception = assertThrows(CoreException.class, () -> UserModel.saveBuilder()
                    .userId(validId)
                    .email(validEmail)
                    .gender(validGender)
                    .birth("2020.01.01")
                    .build()
            );

            assertThat(exception.getMessage()).isEqualTo("생년월일은 yyyy-MM-dd 형식이어야 합니다.");
        }
    }

}
