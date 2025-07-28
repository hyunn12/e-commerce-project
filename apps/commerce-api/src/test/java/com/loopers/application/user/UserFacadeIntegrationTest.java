package com.loopers.application.user;

import com.loopers.domain.point.Point;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserFacadeIntegrationTest {
    // orm --
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private UserFacade userFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 회원_가입_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class Point_객체가_저장된다 {

            @DisplayName("User 객체 저장에 성공한다면")
            @Test
            void user_객체_저장에_성공한다면() {
                // arrange
                String loginId = "test123";
                UserCommand.Join command = new UserCommand.Join(loginId, "test@test.com", "F", "2000-01-01");

                // act
                userFacade.join(command);

                // assert
                Optional<Point> point = pointJpaRepository.findByUserId(loginId);
                assertThat(point).isPresent();
                assertThat(point.get().getPoint()).isEqualTo(0);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class Point_객체가_저장되지_않는다 {

            @DisplayName("User 객체 저장에 실패한다면")
            @Test
            void user_객체_저장에_실패한다면() {
                // arrange
                String loginId = "test123";
                UserCommand.Join command = new UserCommand.Join(loginId, "test@test.com", "F", "birth");

                // act
                assertThrows(CoreException.class, () -> {
                    userFacade.join(command);
                });

                // assert
                assertThat(pointJpaRepository.findByUserId(loginId)).isEmpty();
            }
        }
    }
}
