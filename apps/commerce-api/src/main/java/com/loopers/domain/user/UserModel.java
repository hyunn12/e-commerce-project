package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
        final String PATTERN_USER_ID = "^[a-zA-Z0-9]{1,10}$";
        final String PATTERN_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        final String PATTERN_BIRTH = "^\\d{4}-\\d{2}-\\d{2}$";

        if (userId == null || !userId.matches(PATTERN_USER_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 영문 및 숫자 10자 이내로만 작성해야 합니다.");
        }

        if (email == null || !email.matches(PATTERN_EMAIL)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 xx@yy.zz 형식으로 작성해야 합니다.");
        }

        if (gender == null || !(gender.equals("M") || gender.equals("F"))) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별 형식이 잘못되었습니다.");
        }

        if (birth == null || !birth.matches(PATTERN_BIRTH)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
        }

        this.userId = userId;
        this.email = email;
        this.gender = gender;
        this.birth = LocalDate.parse(birth);
    }

}
