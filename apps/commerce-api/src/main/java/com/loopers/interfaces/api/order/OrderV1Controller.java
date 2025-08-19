package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    public ApiResponse<OrderV1Dto.OrderResponse.Main> order(
            Long userId,
            OrderV1Dto.OrderRequest.Create request
    ) {
        return ApiResponse.success(OrderV1Dto.OrderResponse.Main.from(orderFacade.create(request.toCommand(userId))));
    }

    @Override
    public ApiResponse<OrderV1Dto.OrderResponse.Summary> summary(
            Long userId,
            OrderV1Dto.OrderRequest.Summary request
    ) {
        return ApiResponse.success(OrderV1Dto.OrderResponse.Summary.from(orderFacade.getList(request.toCommand(userId))));
    }

    @Override
    public ApiResponse<OrderV1Dto.OrderResponse.Main> getPoint(Long orderId) {
        return ApiResponse.success(OrderV1Dto.OrderResponse.Main.from(orderFacade.getDetail(orderId)));
    }
}
