package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.loopers.support.utils.Validation.Message.MESSAGE_USER_LOGIN_ID;
import static com.loopers.support.utils.Validation.Pattern.PATTERN_LOGIN_ID;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginId {

    @Column(name = "login_id", nullable = false)
    private String value;

    public static LoginId of(String value) {
        if (value == null || !value.matches(PATTERN_LOGIN_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_USER_LOGIN_ID);
        }
        return new LoginId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
