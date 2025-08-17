package com.loopers.support.utils;

public class Validation {
    private Validation() {}

    public static final class Pattern {
        public static final String PATTERN_LOGIN_ID = "^[a-zA-Z0-9]{1,10}$";
        public static final String PATTERN_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PATTERN_GENDER = "^([MF])$";
        public static final String PATTERN_BIRTH = "^\\d{4}-\\d{2}-\\d{2}$";
    }

    public static final class Message {
        public static final String MESSAGE_PAGINATION_PAGE = "페이지 번호는 0 이상이어야 합니다.";
        public static final String MESSAGE_PAGINATION_SIZE = "페이지 크기는 최소 10, 최대 50까지 가능합니다.";

        public static final String MESSAGE_USER_LOGIN_ID = "아이디는 영문 및 숫자 10자 이내로만 작성해야 합니다.";
        public static final String MESSAGE_USER_LOGIN_ID_EXIST = "이미 존재하는 아이디 입니다.";
        public static final String MESSAGE_USER_EMAIL = "이메일은 xx@yy.zz 형식으로 작성해야 합니다.";
        public static final String MESSAGE_USER_GENDER = "성별은 M 또는 F여야 합니다.";
        public static final String MESSAGE_USER_BIRTH = "생년월일은 yyyy-MM-dd 형식이어야 합니다.";
        public static final String MESSAGE_USER_NOT_FOUND = "회원 정보를 찾을 수 없습니다.";

        public static final String MESSAGE_POINT_CREATE = "포인트는 0 이상이어야 합니다.";
        public static final String MESSAGE_POINT_CHARGE = "충전할 포인트는 0 보다 커야 합니다.";
        public static final String MESSAGE_POINT_USE = "사용할 포인트는 0 보다 커야 합니다.";
        public static final String MESSAGE_POINT_NOT_ENOUGH = "포인트가 부족합니다";
        public static final String MESSAGE_POINT_NOT_FOUND = "포인트 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_POINT_CONFLICT = "포인트 사용에 실패했습니다.";

        public static final String MESSAGE_BRAND_NOT_FOUND = "브랜드 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_PRODUCT_NOT_FOUND = "상품 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_STOCK_NOT_FOUND = "재고 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_STOCK_INVALID_AMOUNT = "차감 수량은 0보다 커야 합니다.";
        public static final String MESSAGE_STOCK_NOT_ENOUGH = "재고가 부족합니다.";

        public static final String MESSAGE_PAYMENT_AMOUNT = "결제 금액은 0 보다 커야 합니다.";

        public static final String MESSAGE_ORDER_TOTAL_AMOUNT = "총금액은 0 보다 커야 합니다.";
        public static final String MESSAGE_ORDER_NOT_FOUND = "주문 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_ORDER_ITEM_EMPTY = "주문 항목이 누락되었습니다.";
        public static final String MESSAGE_ORDER_ITEM_QUANTITY = "수량은 0 보다 커야 합니다.";
        public static final String MESSAGE_ORDER_ITEM_AMOUNT = "금액은 0 보다 커야 합니다.";

        public static final String MESSAGE_COUPON_NOT_FOUND = "쿠폰 정보를 찾을 수 없습니다.";
        public static final String MESSAGE_COUPON_UNUSABLE = "이미 사용했거나 만료된 쿠폰입니다.";
        public static final String MESSAGE_COUPON_MIN_AMOUNT = "최소 주문 금액보다 적어 쿠폰을 사용할 수 없습니다.";
        public static final String MESSAGE_COUPON_INVALID_USER = "쿠폰의 소유자가 아닙니다.";
    }
}
