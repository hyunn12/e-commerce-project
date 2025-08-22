package com.loopers.domain.order;

import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderNoDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class Create_호출_시 {

        @DisplayName("UUID 형식의 주문번호가 생성된다.")
        @Test
        void thenGenerateUUID() {
            // arrange, act
            OrderNo orderNo = OrderNo.create();
            System.out.println("orderNo.toString() = " + orderNo.toString());

            // assert
            assertThat(orderNo).isNotNull();
            assertThatCode(() -> UUID.fromString(orderNo.toString()))
                    .doesNotThrowAnyException();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class From_호출_시 {

        @Test
        @DisplayName("유효한 UUID 문자열이면 OrderNo가 생성된다.")
        void whenValidUUID_thenCreateOrderNo() {
            // arrange
            String uuidStr = UUID.randomUUID().toString();

            // act
            OrderNo orderNo = OrderNo.from(uuidStr);

            // assert
            assertThat(orderNo.toString()).isEqualTo(uuidStr);
        }

        @DisplayName("잘못된 UUID 문자열이면 IllegalArgumentException 예외를 던진다.")
        @Test
        void whenInvalidUUID_thenThrowsException() {
            // arrange
            String orderNo = "test";

            // act, assert
            assertThrows(IllegalArgumentException.class, () -> OrderNo.from(orderNo));
        }

        @Test
        @DisplayName("같은 UUID 라면 같은 OrderNo 객체이다.")
        void whenSameUUID_thenEquals() {
            // arrange
            String uuidStr = UUID.randomUUID().toString();

            // act
            OrderNo orderNo1 = OrderNo.from(uuidStr);
            OrderNo orderNo2 = OrderNo.from(uuidStr);

            // assert
            assertThat(orderNo1).isEqualTo(orderNo2);
            assertThat(orderNo1.hashCode()).isEqualTo(orderNo2.hashCode());
        }
    }
}
