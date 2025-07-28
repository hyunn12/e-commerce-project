package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_EMAIL;
import static com.loopers.support.utils.Validation.Pattern.PATTERN_EMAIL;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    @Column(name = "email", nullable = false)
    private String value;

    public static Email of(String value) {
        if (value == null || !value.matches(PATTERN_EMAIL)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_EMAIL);
        }
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
