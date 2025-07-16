package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserModel;
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
class UserControllerE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserControllerE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            UserJpaRepository userJpaRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.userJpaRepository = userJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class POST {
        private final String requestUrl = "/api/v1/users";

        @DisplayName("API 호출 테스트")
        @Test
        void callApi() {
            // arrange
            UserDto.JoinRequest requestBody = new UserDto.JoinRequest(
                    "test123",
                    "test@test.com",
                    "F",
                    "2000-01-01"
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(requestBody.userId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(requestBody.email()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(requestBody.gender()),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo(requestBody.birth())
            );
        }

        @DisplayName("회원가입 성공 시 생성된 유저 정보를 응답으로 반환함")
        @Test
        void joinSuccess() {
            // arrange
            UserDto.JoinRequest requestBody = new UserDto.JoinRequest(
                    "test123",
                    "test@test.com",
                    "F",
                    "2000-01-01"
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(requestBody.userId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(requestBody.email()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(requestBody.gender()),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo(requestBody.birth())
            );
        }

        @DisplayName("회원가입 시 성별이 없을 경우 400 Bad Request 응답을 반환함")
        @Test
        void joinWithoutGender_returnBadRequest() {
            // arrange
            UserDto.JoinRequest requestBody = new UserDto.JoinRequest(
                    "test123",
                    "test@test.com",
                    null,
                    "2000-01-01"
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL),
                    () -> assertThat(response.getBody().meta().message()).contains("성별 형식이 잘못되었습니다.")
            );
        }

    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GET {

        private final String requestUrl = "/api/v1/users/me";
        private final String userId = "test123";

        @BeforeEach
        void setData() {
            UserModel user = new UserModel(userId, "test@test.com", "F", "2000-01-01");
            userJpaRepository.save(user);
        }

        @DisplayName("API 호출 테스트")
        @Test
        void callApi() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", userId);

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful())
            );
        }

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환함")
        @Test
        void getUserSuccess() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", userId);

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(userId)
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환함")
        @Test
        void getUserWithInvalidUserId_returnNotFound() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", "test");

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

}
