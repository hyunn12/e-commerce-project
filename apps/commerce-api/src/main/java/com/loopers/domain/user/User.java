package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.loopers.support.utils.Validation.Message.*;
import static com.loopers.support.utils.Validation.Pattern.*;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Builder(builderMethodName = "saveBuilder")
    public User(String userId, String email, String gender, String birth) {
        if (userId == null || !userId.matches(PATTERN_USER_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_USER_ID);
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

        this.userId = userId;
        this.email = email;
        this.gender = gender;
        this.birth = LocalDate.parse(birth);
    }
}
