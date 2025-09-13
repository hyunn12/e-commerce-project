package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingCommand;
import com.loopers.application.ranking.RankingInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PAGINATION_PAGE;
import static com.loopers.support.utils.Validation.Message.MESSAGE_PAGINATION_SIZE;

public class RankingV1Dto {

    public static class RankingRequest {

        public record Summary(
                @Pattern(regexp = "\\d{8}", message = "날짜는 yyyyMMdd 형식이어야 합니다.")
                String date,
                @Min(value = 0, message = MESSAGE_PAGINATION_PAGE)
                int page,
                @Min(value = 10, message = MESSAGE_PAGINATION_SIZE)
                @Max(value = 50, message = MESSAGE_PAGINATION_SIZE)
                int size
        ) {
            public RankingCommand.Summary toCommand() {
                return RankingCommand.Summary.builder()
                        .date(date)
                        .page(page)
                        .size(size)
                        .build();
            }
        }
    }

    public static class RankingResponse {

        public record Summary(
                List<Item> rankings,
                int page,
                int size,
                long totalCount
        ) {
            public static RankingResponse.Summary from(RankingInfo.Summary info) {
                List<Item> rankings = info.getRankings().stream()
                        .map(Item::from)
                        .toList();

                return new RankingResponse.Summary(
                        rankings,
                        info.getPage(),
                        info.getSize(),
                        info.getTotalCount()
                );
            }
        }

        public record Item(
                int rank,
                Long productId,
                String productName,
                int price,
                String brandName,
                double score
        ) {
            public static RankingResponse.Item from(RankingInfo.Item item) {
                return new RankingResponse.Item(
                        item.getRank(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getBrandName(),
                        item.getScore()
                );
            }
        }
    }
}
