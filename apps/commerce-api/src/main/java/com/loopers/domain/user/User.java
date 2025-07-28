package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

import static com.loopers.support.utils.Validation.Message.*;
import static com.loopers.support.utils.Validation.Pattern.*;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Builder(builderMethodName = "saveBuilder")
    public User(String loginId, String email, String gender, String birth) {
        if (loginId == null || !loginId.matches(PATTERN_LOGIN_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_LOGIN_ID);
        }

        if (email == null || !email.matches(PATTERN_EMAIL)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_EMAIL);
        }

        if (gender == null || !gender.matches(PATTERN_GENDER)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_GENDER);
        }

        if (birth == null || !birth.matches(PATTERN_BIRTH)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_BIRTH);
        }

        this.loginId = loginId;
        this.email = email;
        this.gender = gender;
        this.birth = LocalDate.parse(birth);
    }
}
