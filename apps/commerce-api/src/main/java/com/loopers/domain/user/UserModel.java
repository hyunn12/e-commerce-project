package com.loopers.domain.user;

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
@Table(name = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String email;

    private String gender;

    private LocalDate birth;

    @Builder(builderMethodName = "saveBuilder")
    public UserModel(String userId, String email, String gender, String birth) {
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
