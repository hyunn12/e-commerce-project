package com.loopers.support.utils;

public class Validation {
    private Validation() {}

    public static final class Pattern {
        public static final String PATTERN_USER_ID = "^[a-zA-Z0-9]{1,10}$";
        public static final String PATTERN_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PATTERN_GENDER = "^([MF])$";
        public static final String PATTERN_BIRTH = "^\\d{4}-\\d{2}-\\d{2}$";
    }

    public static final class Message {
        public static final String MESSAGE_USER_ID = "아이디는 영문 및 숫자 10자 이내로만 작성해야 합니다.";
        public static final String MESSAGE_EMAIL = "이메일은 xx@yy.zz 형식으로 작성해야 합니다.";
        public static final String MESSAGE_GENDER = "성별은 M 또는 F여야 합니다.";
        public static final String MESSAGE_BIRTH = "생년월일은 yyyy-MM-dd 형식이어야 합니다.";
        public static final String MESSAGE_POINT_CREATE = "포인트는 0 이상이어야 합니다.";
        public static final String MESSAGE_POINT_CHARGE = "충전할 포인트는 0 보다 커야 합니다.";
    }
}
