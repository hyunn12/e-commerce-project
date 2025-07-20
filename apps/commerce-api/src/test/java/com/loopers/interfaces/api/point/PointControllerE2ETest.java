package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointControllerE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final PointJpaRepository pointJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public PointControllerE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            PointJpaRepository pointJpaRepository,
            UserJpaRepository userJpaRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.pointJpaRepository = pointJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class POST {
        private final String requestUrl = "/api/v1/points/charge";

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 충전에_성공한_후_충전_결과를_받는다 {

            @DisplayName("존재하는 userId로 1000원을 충전한다면")
            @Test
            void 존재하는_userId로_1000원을_충전한다면() {
                // arrange
                final String userId = "test123";
                final int amount = 1000;
                User user = new User(userId, "test@test.com", "F", "2000-01-01");
                userJpaRepository.save(user);
                Point point = new Point(userId, 10000);
                pointJpaRepository.save(point);

                PointDto.ChargeRequest requestBody = new PointDto.ChargeRequest(amount);
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-USER-ID", userId);

                // act
                ParameterizedTypeReference<ApiResponse<PointDto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<PointDto.PointResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().amount()).isEqualTo(point.getPoint()+amount)
                );
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 충전에_실패한_후_404_Not_Found_응답을_받는다 {

            @DisplayName("주어진 userId의 회원이 존재하지 않는 회원이라면")
            @Test
            void 주어진_userId의_회원이_존재하지_않는_회원이라면() {
                // arrange
                PointDto.ChargeRequest requestBody = new PointDto.ChargeRequest(1000);
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-USER-ID", "test123");

                // act
                ParameterizedTypeReference<ApiResponse<PointDto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<PointDto.PointResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), responseType);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                        () -> assertThat(response.getBody()).isNotNull(),
                        () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL),
                        () -> assertThat(response.getBody().meta().message()).contains("포인트 정보를 찾을 수 없습니다.")
                );
            }
        }
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class GET {
        private final String requestUrl = "/api/v1/points";

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 보유_포인트를_받는다 {

            @DisplayName("포인트 조회에 성공한다면")
            @Test
            void 포인트_조회에_성공한다면() {
                // arrange
                final String userId = "test123";
                final int currentPoint = 10000;
                User user = new User(userId, "test@test.com", "F", "2000-01-01");
                userJpaRepository.save(user);
                Point point = new Point(userId, currentPoint);
                pointJpaRepository.save(point);

                HttpHeaders headers = new HttpHeaders();
                headers.add("X-USER-ID", userId);

                // act
                ParameterizedTypeReference<ApiResponse<PointDto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<PointDto.PointResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().amount()).isEqualTo(currentPoint)
                );
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_실패한_후_404_Not_Found_응답을_받는다 {

            @DisplayName("X-USER-ID 헤더가 없을 경우")
            @Test
            void X_USER_ID_헤더가_없을_경우() {
                // arrange, act
                ParameterizedTypeReference<ApiResponse<PointDto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<PointDto.PointResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

                // assert
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            }
        }
    }
}
