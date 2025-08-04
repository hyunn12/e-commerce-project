# 04. ERD

### 데이터 목록
- **USER**:  사용자
- **POINT**: 포인트
- **POINT_HISTORY**: 포인트 이력
- **LIKE**: 좋아요
- **BRAND**: 브랜드
- **PRODUCT**: 상품
- **STOCK**: 재고
- **ORDER**: 주문
- **ORDER_ITEM**: 주문 상세
- **PAYMENT**: 결제
- **COUPON**: 쿠폰
- **USER_COUPON**: 사용자 쿠폰

### ERD
```mermaid  
erDiagram  
    USER {  
        BIGINT ID PK "사용자 ID"  
        VARCHAR LOGIN_ID "아이디"
        VARCHAR EMAIL "이메일"
        VARCHAR GENDER "성별"
        DATETIME BIRTH "생년월일"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
  
    POINT {  
        BIGINT ID PK "ID"
        BIGINT USER_ID FK "사용자 ID"  
        INT POINT "포인트"  
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
  
    POINT_HISTORY {  
        BIGINT ID PK "ID"
        BIGINT USER_ID FK "사용자 ID"  
        INT POINT "포인트"
        VARCHAR TYPE "유형 (CHARGE, USE)"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }

    LIKE {  
        BIGINT ID PK "ID"
        BIGINT USER_ID FK "사용자 ID"  
        INT PRODUCT_ID FK "상품 ID"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
  
    BRAND {  
        BIGINT ID PK "ID"  
        VARCHAR NAME "브랜드명"  
        VARCHAR DESCRIPTION "브랜드설명"  
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
        
    PRODUCT {  
        BIGINT ID PK "ID"
        BIGINT BRAND_ID FK "브랜드 ID"  
        VARCHAR NAME "상품명"  
        INT PRICE "가격"
        INT LIKE_COUNT "좋아요 개수"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }

    STOCK {
        BIGINT ID PK "ID"
        BIGINT PRODUCT_ID FK "상품 ID"
        INT QUANTITY "수량"
        DATETIME CREATED_AT "생성일"
        DATETIME UPDATED_AT "수정일"
        DATETIME DELETED_AT "삭제일"
    }

    ORDER {  
        BIGINT ID PK "ID"
        BIGINT USER_ID FK "사용자 ID"
        INT COUPON_ID FK "쿠폰 ID"
        INT TOTAL_AMOUNT "총금액"  
        VARCHAR STATUS "주문상태 (INIT, SUCCESS, CANCEL)" 
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
        
    ORDER_ITEM {  
        BIGINT ID PK "ID"
        BIGINT ORDER_ID FK "주문 ID"
        BIGINT PRODUCT_ID FK "상품 ID"  
        INT QUANTITY "수량"
        INT AMOUNT "금액"
        INT SUBTOTAL "합계"  
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
        
    PAYMENT {  
        BIGINT ID PK "ID"
        BIGINT USER_ID FK "사용자 ID"  
        INT PAYMENT_AMOUNT "결제금액"  
        VARCHAR STATUS "결제상태 (SUCCESS, FAIL, CANCEL)"  
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }
    
    COUPON {
        BIGINT ID PK "ID"
        VARCHAR NAME "쿠폰명"
        INT MAX_COUNT "최대개수"
        INT ISSUED_COUNT "발급개수"
        VARCHAR DISCOUNT_TYPE "할인유형 (PRICE, RATE)"
        INT DISCOUNT_VALUE "할인값"
        INT MIN_AMOUNT "최소금액"
        DATETIME CREATED_AT "생성일"
        DATETIME UPDATED_AT "수정일"
        DATETIME DELETED_AT "삭제일"
    }

    USER_COUPON {
        BIGINT ID PK "ID"
        INT COUPON_ID FK "쿠폰 ID"
        INT USER_ID FK "사용자 ID"
        VARCHAR STATUS "쿠폰상태 (UNUSED, USED, EXPIRED)"
        DATETIME CREATED_AT "생성일"
        DATETIME USED_AT "사용일"
        DATETIME EXPIRE_AT "만료일"
        DATETIME UPDATED_AT "수정일"
        DATETIME DELETED_AT "삭제일"
    }

    BRAND ||--o{ PRODUCT : "1:N"
    PRODUCT ||--|| STOCK : "1:1"
    USER ||--o{ ORDER : "1:N"
    PRODUCT ||--o{ ORDER_ITEM : "1:N"
    ORDER ||--o{ ORDER_ITEM : "1:N"
    PRODUCT ||--o{ LIKE : "1:N"
    POINT ||--o{ POINT_HISTORY : "1:N"
    USER ||--|| POINT : "1:1"
    USER ||--o{ LIKE : "1:N"
    USER ||--o{ PAYMENT : "1:N"
    USER ||--o{ USER_COUPON : "1:N"
    COUPON ||--o{ USER_COUPON : "1:N"

```
