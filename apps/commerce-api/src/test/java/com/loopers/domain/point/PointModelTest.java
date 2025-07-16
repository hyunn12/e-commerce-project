package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointModelTest {

    @DisplayName("유효한 포인트 충전 시 정상적으로 포인트가 충전됨")
    @Test
    void addValidPoint() {
        // arrange
        int current = 10000;
        int amount = 30000;

        // act
        PointModel pointModel = new PointModel("test123", current);
        pointModel.addPoint(amount);

        // assert
        assertThat(pointModel.getPoint()).isEqualTo(current+amount);
    }

    @DisplayName("0 이하의 정수로 포인트를 충전 시 예외 발생")
    @Test
    void addInvalidPoint_returnBadRequest() {
        // arrange
        int current = 10000;
//        int amount = -10000;
        int amount = 0;

        // act
        PointModel pointModel = new PointModel("test123", current);

        CoreException exception = assertThrows(CoreException.class, () -> pointModel.addPoint(amount));

        // assert
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }
}
