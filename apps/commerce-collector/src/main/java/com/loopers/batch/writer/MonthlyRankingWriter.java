package com.loopers.batch.writer;

import com.loopers.domain.ranking.ProductMonthlyRanking;
import com.loopers.domain.ranking.ProductMonthlyRankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyRankingWriter implements ItemWriter<ProductMonthlyRanking> {

    private final ProductMonthlyRankingRepository monthlyRankingRepository;

    @Override
    public void write(Chunk<? extends ProductMonthlyRanking> chunk) {

        if (!chunk.isEmpty()) {
            List<ProductMonthlyRanking> items = new ArrayList<>(chunk.getItems());
            monthlyRankingRepository.saveAll(items);
            log.info("Saved {} monthly ranking records", chunk.size());
        }
    }
}
