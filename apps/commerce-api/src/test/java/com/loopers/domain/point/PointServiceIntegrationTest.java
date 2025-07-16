package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("포인트 충전")
    class Charge {

        @DisplayName("유효한 포인트 충전 시 정상적으로 포인트가 충전됨")
        @Test
        void chargeWithValidPoint() {
            // arrange
            int current = 10000;
            int amount = 20000;

            UserModel user = new UserModel("test123", "test1@test.com", "F", "2000-01-01");
            userJpaRepository.save(user);
            PointModel point = new PointModel(user.getUserId(), current);
            pointJpaRepository.save(point);

            PointModel added = new PointModel(user.getUserId(), amount);

            // act
            PointModel charged = pointService.charge(added);

            // assert
            Optional<PointModel> saved = pointJpaRepository.findByUserId(user.getUserId());
            assertThat(saved).isPresent();
            assertThat(saved.get().getPoint()).isEqualTo(current+amount);
            assertThat(charged.getPoint()).isEqualTo(current+amount);
        }

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우 예외 발생")
        @Test
        void chargeWithInvalidUserId_returnNotFound() {
            // arrange
            PointModel added = new PointModel("test", 10000);

            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    pointService.charge(added)
            );

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("포인트 조회")
    class GetPoint {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트를 반환함")
        @Test
        void getPointWithValidId() {
            // arrange
            String userId = "test123";
            int point = 10000;
            pointJpaRepository.save(new PointModel(userId, point));

            // act
            PointModel result = pointService.getPointByUserId(userId);

            // assert
            assertThat(result).isNotNull();
            assertThat(result.getPoint()).isEqualTo(point);
        }

        @Test
        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null이 반환됨")
        void getPointWithInvalidUserId_returnNull() {
            // act
            PointModel result = pointService.getPointByUserId("test123");

            // assert
            assertThat(result).isNull();
        }

    }
}
