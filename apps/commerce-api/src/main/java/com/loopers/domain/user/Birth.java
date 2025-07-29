package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_USER_BIRTH;
import static com.loopers.support.utils.Validation.Pattern.PATTERN_BIRTH;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Birth {

    @Column(name = "birth", nullable = false)
    private String value;

    public static Birth of(String value) {
        if (value == null || !value.matches(PATTERN_BIRTH)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_USER_BIRTH);
        }
        return new Birth(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
