import http from "k6/http";

const VUSER = Number(__ENV.VUSER || 25);

/**
 * 상품 목록 조회 API 부하 테스트
 * - API 처리량과 응답 속도 측정
 * - 요청 본문 파싱 없이 순수 속도 중심 성능 측정
 * - 대량 더미데이터(DummyDataGenerator) 기반 환경에서 실행
 */
export const options = {
    stages: [
        { duration: "1m", target: VUSER }
    ],
};

export default function () {
    const brandId = [1, 2, 3, 4, 5][Math.floor(Math.random() * 5)]; // 가장 상품이 많은 상위 5개
    const sort = "LIKES_DESC"; // 좋아요순 정렬
    const page = Math.floor(Math.random() * 10);
    const size = 20;

    http.get(
        `http://localhost:8080/api/v1/products?brandId=${brandId}&sort=${sort}&page=${page}&size=${size}`,
        { tags: { name: "GET /api/v1/products" }, responseType: "none", timeout: "5s" }
    );
}
