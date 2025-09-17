package com.loopers.batch.writer;

import com.loopers.domain.ranking.ProductWeeklyRanking;
import com.loopers.domain.ranking.ProductWeeklyRankingRepository;
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
public class WeeklyRankingWriter implements ItemWriter<ProductWeeklyRanking> {

    private final ProductWeeklyRankingRepository weeklyRankingRepository;

    @Override
    public void write(Chunk<? extends ProductWeeklyRanking> chunk) {

        if (!chunk.isEmpty()) {
            List<ProductWeeklyRanking> items = new ArrayList<>(chunk.getItems());
            weeklyRankingRepository.saveAll(items);
        }
    }
}
