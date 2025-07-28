package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;

import java.util.Arrays;

import static com.loopers.support.utils.Validation.Message.MESSAGE_GENDER;

@Getter
public enum Gender {
    MALE("M"),
    FEMALE("F");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public static Gender fromValue(String value) {
        return Arrays.stream(Gender.values())
                .filter(gender -> gender.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, MESSAGE_GENDER));
    }
}
