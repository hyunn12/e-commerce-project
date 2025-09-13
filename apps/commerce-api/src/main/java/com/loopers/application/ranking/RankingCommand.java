package com.loopers.application.ranking;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingCommand {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {

        private String date;
        @Builder.Default private int page = 0;
        @Builder.Default private int size = 20;

        public Pageable toPageable() {
            return PageRequest.of(page, size);
        }
    }
}
