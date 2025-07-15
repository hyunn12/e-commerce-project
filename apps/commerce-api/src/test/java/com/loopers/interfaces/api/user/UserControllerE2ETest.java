package com.loopers.interfaces.api.user;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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

        @DisplayName("API 호출 테스트")
        @Test
        void callApi() {
            // arrange
            String requestUrl = "/api/v1/users";
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
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GET {

        @DisplayName("API 호출 테스트")
        @Test
        void callApi() {
            // arrange
            String requestUrl = "/api/v1/users/me";

            // act
            ParameterizedTypeReference<ApiResponse<UserDto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserDto.UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful())
            );
        }

    }

}
