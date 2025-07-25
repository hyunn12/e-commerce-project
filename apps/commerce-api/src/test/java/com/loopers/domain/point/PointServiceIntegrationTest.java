package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PointServiceIntegrationTest {
    // orm --
    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private PointService pointService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_충전_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 정상적으로_포인트가_충전된다 {

            @DisplayName("유효한 포인트라면")
            @Test
            void 유효한_포인트라면() {
                // arrange
                int current = 10000;
                int amount = 20000;

                User user = new User("test123", "test1@test.com", "F", "2000-01-01");
                userJpaRepository.save(user);
                Point point = new Point(user.getUserId(), current);
                pointJpaRepository.save(point);

                Point added = new Point(user.getUserId(), amount);

                // act
                pointService.charge(added);

                // assert
                Optional<Point> saved = pointJpaRepository.findByUserId(user.getUserId());
                assertThat(saved.get().getPoint()).isEqualTo(current+amount);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 충전에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("존재하지 않는 유저 아이디라면")
            @Test
            void 존재하지_않는_유저_아이디라면() {
                // arrange
                Point added = new Point("test", 10000);

                // act
                CoreException exception = assertThrows(CoreException.class, () ->
                        pointService.charge(added)
                );

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_조회_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 보유_포인트를_반환한다 {

            @DisplayName("주어진 userId의 회원이 존재하는 회원이라면")
            @Test
            void 주어진_userId의_회원이_존재하는_회원이라면() {
                // arrange
                String userId = "test123";
                int point = 10000;
                pointJpaRepository.save(new Point(userId, point));

                // act
                Point result = pointService.getPointByUserId(userId);

                // assert
                assertThat(result.getPoint()).isEqualTo(point);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class Null을_반환한다 {

            @DisplayName("주어진 userId의 회원이 존재하지 않는 회원이라면")
            @Test
            void 주어진_userId의_회원이_존재하지_않는_회원이라면() {
                // act
                Point result = pointService.getPointByUserId("test123");

                // assert
                assertThat(result).isNull();
            }
        }
    }
}
