package com.loopers.domain.point;

import com.loopers.domain.user.*;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PointServiceIntegrationTest {
    // orm --
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;
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

                User user = User.saveBuilder()
                        .loginId(LoginId.of("test123"))
                        .email(Email.of("test1@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();
                userJpaRepository.save(user);
                Point point = new Point(user.getId(), current);
                pointJpaRepository.save(point);

                Point added = new Point(user.getId(), amount);

                // act
                pointService.charge(added);

                // assert
                Optional<Point> saved = pointJpaRepository.findByUserId(user.getId());
                assertThat(saved.get().getPoint()).isEqualTo(current+amount);

                List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(point.getUserId());
                assertThat(histories).hasSize(1);
                assertThat(histories.get(0).getAmount()).isEqualTo(amount);
                assertThat(histories.get(0).getType()).isEqualTo(PointType.CHARGE);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 충전에_실패한_후_404_Not_Found_예외가_발생한다 {

            @DisplayName("존재하지 않는 유저 아이디라면")
            @Test
            void 존재하지_않는_유저_아이디라면() {
                // arrange
                Point added = new Point(1L, 10000);

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
                Long userId = 1L;
                int point = 10000;
                pointJpaRepository.save(new Point(userId, point));

                // act
                Point result = pointService.getDetailByUserId(userId);

                // assert
                assertThat(result.getPoint()).isEqualTo(point);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_실패하고_Not_Found_예외를_반환한다 {

            @DisplayName("주어진 userId의 회원이 존재하지 않는 회원이라면")
            @Test
            void 주어진_userId의_회원이_존재하지_않는_회원이라면() {
                // act
                CoreException exception = assertThrows(CoreException.class, () -> pointService.getDetailByUserId(1L));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_사용_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 정상적으로_포인트가_사용된다 {

            @DisplayName("유효한 포인트라면")
            @Test
            void 유효한_포인트라면() {
                // arrange
                int current = 30000;
                int amount = 20000;

                User user = User.saveBuilder()
                        .loginId(LoginId.of("test123"))
                        .email(Email.of("test1@test.com"))
                        .gender(Gender.fromValue("F"))
                        .birth(Birth.of("2000-01-01"))
                        .build();
                userJpaRepository.save(user);
                Point point = new Point(user.getId(), current);
                pointJpaRepository.save(point);

                // act
                pointService.use(user.getId(), amount);

                // assert
                Optional<Point> saved = pointJpaRepository.findByUserId(user.getId());
                assertThat(saved.get().getPoint()).isEqualTo(current-amount);

                List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(point.getUserId());
                assertThat(histories).hasSize(1);
                assertThat(histories.get(0).getAmount()).isEqualTo(amount);
                assertThat(histories.get(0).getType()).isEqualTo(PointType.USE);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 충전에_실패한_후_404_Not_Found_예외가_발생한다 {

            @DisplayName("존재하지 않는 유저 아이디라면")
            @Test
            void 존재하지_않는_유저_아이디라면() {
                // act
                CoreException exception = assertThrows(CoreException.class, () ->
                        pointService.use(1L, 10000)
                );

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            }
        }
    }

}
