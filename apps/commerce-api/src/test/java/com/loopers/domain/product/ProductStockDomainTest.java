package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductStockDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 재고_감소_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 정상적으로_재고가_감소한다 {

            @DisplayName("정상적인 수량이 주어진다면")
            @Test
            void whenQuantityIsValid() {
                // arrange
                int initQuantity = 100;
                int decreaseQuantity = 5;
                Product product = Instancio.create(Product.class);
                ProductStock stock = new ProductStock(product, initQuantity);

                // act
                stock.decrease(decreaseQuantity);

                // assert
                assertThat(stock.getQuantity()).isEqualTo(initQuantity-decreaseQuantity);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 재고_감소에_실패한_후_409_Conflict_예외가_발생한다 {

            @DisplayName("재고보다 많은 수량이 주어진다면")
            @Test
            void whenQuantityExceedsStock() {
                // arrange
                int initQuantity = 10;
                int decreaseQuantity = 500;
                Product product = Instancio.create(Product.class);
                ProductStock stock = new ProductStock(product, initQuantity);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> stock.decrease(decreaseQuantity));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 재고_감소에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("0 이하의 수량이 주어진다면")
            @Test
            void whenQuantityUnderZero() {
                // arrange
                int initQuantity = 100;
                int decreaseQuantity = 0;
                Product product = Instancio.create(Product.class);
                ProductStock stock = new ProductStock(product, initQuantity);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> stock.decrease(decreaseQuantity));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }
    }
}
