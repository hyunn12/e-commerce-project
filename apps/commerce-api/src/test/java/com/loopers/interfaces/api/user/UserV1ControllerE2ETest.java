package com.loopers.interfaces.api.user;

import com.loopers.domain.user.*;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ControllerE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserV1ControllerE2ETest(
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

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_성공한_후_유저_정보를_받는다 {

            @DisplayName("회원가입에 성공한다면")
            @Test
            void 회원_가입에_성공한다면() {
                // arrange
                UserV1Dto.JoinRequest requestBody = new UserV1Dto.JoinRequest(
                        "test123",
                        "test@test.com",
                        "F",
                        "2000-01-01"
                );

                // act
                ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().loginId()).isEqualTo(requestBody.loginId()),
                        () -> assertThat(response.getBody().data().email()).isEqualTo(requestBody.email()),
                        () -> assertThat(response.getBody().data().gender()).isEqualTo(requestBody.gender()),
                        () -> assertThat(response.getBody().data().birth()).isEqualTo(requestBody.birth())
                );
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 회원_가입에_실패한_후_400_Bad_Request_응답을_받는다 {

            @DisplayName("성별이 없다면")
            @Test
            void 성별이_없다면() {
                // arrange
                UserV1Dto.JoinRequest requestBody = new UserV1Dto.JoinRequest(
                        "test123",
                        "test@test.com",
                        null,
                        "2000-01-01"
                );

                // act
                ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
                );
            }
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GET {

        private final String requestUrl = "/api/v1/users/me";
        private Long userId;

        @BeforeEach
        void setData() {
            User user = User.saveBuilder()
                    .loginId(LoginId.of("test123"))
                    .email(Email.of("test1@test.com"))
                    .gender(Gender.fromValue("F"))
                    .birth(Birth.of("2000-01-01"))
                    .build();
            userJpaRepository.save(user);
            userId = user.getId();
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_성공한_후_유저_정보를_받는다 {

            @DisplayName("주어진 loginId의 회원이 존재하는 회원이라면")
            @Test
            void 주어진_loginId의_회원이_존재하는_회원이라면() {
                // arrange
                HttpHeaders headers = new HttpHeaders();
                headers.add(USER_USER_ID_HEADER, userId.toString());

                // act
                ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(userId)
                );
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_실패한_후_404_Not_Found_응답을_받는다 {

            @DisplayName("주어진 loginId의 회원이 존재하지 않는 회원이라면")
            @Test
            void 주어진_loginId의_회원이_존재하지_않는_회원이라면() {
                // arrange
                HttpHeaders headers = new HttpHeaders();
                headers.add(USER_USER_ID_HEADER, Long.toString(Long.MAX_VALUE));

                // act
                ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
                );
            }
        }
    }
}
