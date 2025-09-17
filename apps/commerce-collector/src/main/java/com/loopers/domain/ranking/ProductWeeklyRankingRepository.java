package com.loopers.domain.ranking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductWeeklyRankingRepository extends JpaRepository<ProductWeeklyRanking, Long> {

    /**
     * 특정 주차의 랭킹 데이터 삭제 (배치 재실행 대비)
     */
    @Modifying
    @Query("DELETE FROM ProductWeeklyRanking pwr WHERE pwr.yearWeek = :yearWeek")
    void deleteByYearWeek(@Param("yearWeek") String yearWeek);

    /**
     * 특정 주차의 랭킹 데이터 조회
     */
    @Query("SELECT pwr FROM ProductWeeklyRanking pwr WHERE pwr.yearWeek = :yearWeek ORDER BY pwr.totalScore DESC")
    List<ProductWeeklyRanking> findByYearWeekOrderByTotalScoreDesc(@Param("yearWeek") String yearWeek);

    /**
     * 특정 주차의 랭킹 데이터 개수 조회
     */
    long countByYearWeek(String yearWeek);

    /**
     * 특정 상품의 특정 주차 랭킹 조회
     */
    ProductWeeklyRanking findByProductIdAndYearWeek(Long productId, String yearWeek);
}
