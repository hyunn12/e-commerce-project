package com.loopers.application.user;

import com.loopers.domain.point.PointModel;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("user 저장 성공 시 point 같이 저장됨")
    @Test
    void whenJoinSuccess_thenPointSaved() {
        // arrange
        String userId = "test123";
        UserInfo user = new UserInfo(null, userId, "test@test.com", "F", "2000-01-01");

        // act
        userFacade.join(user);

        // assert
        Optional<PointModel> point = pointJpaRepository.findByUserId(userId);
        assertThat(point).isPresent();
        assertThat(point.get().getPoint()).isEqualTo(0);
    }

    @DisplayName("user 저장 실패 시 point 저장되지 않음")
    @Test
    void whenJoinFail_thenPointNotSaved() {
        // arrange
        String userId = "test123";
        UserInfo user = new UserInfo(null, userId, "test@test.com", "F", "birth");

        // act
        assertThrows(CoreException.class, () -> {
            userFacade.join(user);
        });

        // assert
        assertThat(pointJpaRepository.findByUserId(userId)).isEmpty();
    }
}
