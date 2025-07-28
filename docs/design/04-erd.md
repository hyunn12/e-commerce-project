# 04. ERD

### 데이터 목록
- **USER**:  사용자
- **POINT**: 포인트
- **POINT_HISTORY**: 포인트 이력
- **LIKE**: 좋아요
- **BRAND**: 브랜드
- **PRODUCT**: 상품
- **PRODUCT_STOCK**: 재고
- **ORDER**: 주문
- **ORDER_ITEM**: 주문 상세
- **PAYMENT**: 결제

### ERD
```mermaid  
erDiagram  
    USER {  
        BIGINT ID PK "사용자 ID"  
        VARCHAR USER_ID "아이디"
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
        BOOLEAN IS_LIKED "좋아요 여부"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
  
    BRAND {  
        BIGINT ID PK "ID"  
        VARCHAR NAME "브랜드명"  
        VARCHAR DESC "브랜드설명"  
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }  
        
    PRODUCT {  
        BIGINT ID PK "ID"
        BIGINT BRAND_ID FK "브랜드 ID"  
        VARCHAR NAME "상품명"  
        INT PRICE "가격"
        DATETIME CREATED_AT "생성일"  
        DATETIME UPDATED_AT "수정일"  
        DATETIME DELETED_AT "삭제일"  
    }

    PRODUCT_STOCK {
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
        INT SUBTOTAL "소계"  
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
    
    BRAND ||--o{ PRODUCT : "1:N"
    PRODUCT ||--|| PRODUCT_STOCK : "1:1"
    USER ||--o{ ORDER : "1:N"
    PRODUCT ||--o{ ORDER_ITEM : "1:N"
    ORDER ||--o{ ORDER_ITEM : "1:N"
    PRODUCT ||--o{ LIKE : "1:N"
    POINT ||--o{ POINT_HISTORY : "1:N"
    USER ||--|| POINT : "1:1"
    USER ||--o{ LIKE : "1:N"
    USER ||--o{ PAYMENT : "1:N"
  
```
