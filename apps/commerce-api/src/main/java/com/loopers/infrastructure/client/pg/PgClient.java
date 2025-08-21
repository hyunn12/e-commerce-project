package com.loopers.infrastructure.client.pg;

import com.loopers.interfaces.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.loopers.support.constants.HeaderConstants.PG_HEADER;

@FeignClient(name = "pgApiClient", url = "${client.pg-simulator.url.ver-1}")
public interface PgClient {

    @PostMapping("/payments")
    ApiResponse<PgClientDto.PgResponse> request(
            @RequestBody PgClientDto.PgRequest request,
            @RequestHeader(PG_HEADER) Long userId
    );
}
