import { check } from "k6";
import { ProductV1ApiClient } from "../openapi-to-k6-generate/product-v1-api.ts";

export const options = {
    stages: [
        { duration: "30s", target: 10 },
        { duration: "1m",  target: 30 },
        { duration: "2m",  target: 50 },
        { duration: "1m",  target: 30 },
        { duration: "30s", target: 0 },
    ],
};

export default function () {

    const client = new ProductV1ApiClient({
        baseUrl: __ENV.BASE_URL || "http://localhost:8080",
    });

    const BRAND_IDS = [1, 2, 3, 4, 5];
    const SORTS = ["LATEST", "PRICE_ASC", "LIKES_DESC"];
    const brandId = BRAND_IDS[Math.floor(Math.random() * BRAND_IDS.length)];
    const sort = SORTS[Math.floor(Math.random() * SORTS.length)];
    const page = Math.floor(Math.random() * 50);
    const size = 20;

    const result = client.getList(
        { brandId, sort, page, size },
        { tags: { name: "GET /api/v1/products" } }
    );
    const resp = result.response;

    const isOK = check(resp, { "목록 조회 200": (r) => r.status === 200 });
    if (!isOK) {
        console.warn(`Non-200 status=${resp.status}, body=${String(resp.body).slice(0, 200)}...`);
        return;
    }

    let body = result.data;
    if (body === undefined) {
        try {
            body = resp.json ? resp.json() : JSON.parse(resp.body);
        } catch (e) {
            check(null, { "json parse ok": () => false });
            console.error(`JSON parse error: ${e}. body=${String(resp.body).slice(0,200)}...`);
            return;
        }
    }

    const structureOk = check(body, {
        "meta exists":       (b) => !!b?.meta,
        "result SUCCESS":    (b) => b?.meta?.result === "SUCCESS",
        "data.products arr": (b) => Array.isArray(b?.data?.products),
    });
    if (!structureOk) {
        console.error(`Unexpected structure: ${JSON.stringify(body).slice(0, 300)}...`);
        return;
    }

    const products = body.data.products || [];
    check(products, { "has items (<= size)": (ps) => ps.length >= 0 && ps.length <= size });

    if (products.length > 0) {
        const p0 = products[0];
        check(p0, {
            "product has id":   (p) => typeof p?.id === "number",
            "product has name": (p) => typeof p?.name === "string",
            "product has price":(p) => typeof p?.price === "number",
        });
    }
}
