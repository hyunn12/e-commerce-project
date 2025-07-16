package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환함")
        @Test
        void charge1000WithValidUser_returnPoint() {
            // arrange
            final String userId = "test123";
            final int amount = 1000;
            UserModel user = new UserModel(userId, "test@test.com", "F", "2000-01-01");
            userJpaRepository.save(user);
            PointModel point = new PointModel(userId, 10000);
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
                    () -> assertThat(response.getBody().data().point()).isEqualTo(point.getPoint()+amount)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답 반환함")
        @Test
        void chargeWithInvalidUser_returnNotFound() {
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

    @DisplayName("GET /api/v1/points")
    @Nested
    class GET {
        private final String requestUrl = "/api/v1/points";

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 반환함")
        @Test
        void getPointWithValidUser_returnPoint() {
            // arrange
            final String userId = "test123";
            final int currentPoint = 10000;
            UserModel user = new UserModel(userId, "test@test.com", "F", "2000-01-01");
            userJpaRepository.save(user);
            PointModel point = new PointModel(userId, currentPoint);
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
                    () -> assertThat(response.getBody().data().point()).isEqualTo(currentPoint)
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환함")
        @Test
        void getPointWithoutHeader_returnBadRequest() {
            // arrange, act
            ParameterizedTypeReference<ApiResponse<PointDto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointDto.PointResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
