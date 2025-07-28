package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Embedded
    private LoginId loginId;

    @Embedded
    private Email email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Birth birth;

    @Builder(builderMethodName = "saveBuilder")
    public User(LoginId loginId, Email email, Gender gender, Birth birth) {
        this.loginId = loginId;
        this.email = email;
        this.gender = gender;
        this.birth = birth;
    }
}
