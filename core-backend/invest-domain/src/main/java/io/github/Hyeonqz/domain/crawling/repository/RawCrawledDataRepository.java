package io.github.Hyeonqz.domain.crawling.repository;

import io.github.Hyeonqz.domain.crawling.entity.RawCrawledData;
import io.github.Hyeonqz.domain.crawling.enums.CrawlingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 크롤링 원본 데이터 Repository
 */
@Repository
public interface RawCrawledDataRepository extends JpaRepository<RawCrawledData, Long> {

    /**
     * URL로 크롤링 데이터 조회 (중복 체크용)
     * @param sourceUrl 소스 URL
     * @return 크롤링 데이터
     */
    Optional<RawCrawledData> findBySourceUrl(String sourceUrl);

    /**
     * 상태별 크롤링 데이터 조회
     * @param status 처리 상태
     * @return 크롤링 데이터 목록
     */
    List<RawCrawledData> findByStatus(CrawlingStatus status);

    /**
     * 특정 기간 내 크롤링 데이터 조회
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 크롤링 데이터 목록
     */
    @Query("SELECT r FROM RawCrawledData r WHERE r.crawledAt BETWEEN :startDate AND :endDate ORDER BY r.crawledAt DESC")
    List<RawCrawledData> findByCrawledAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 상태별 데이터 개수 조회
     * @param status 처리 상태
     * @return 데이터 개수
     */
    long countByStatus(CrawlingStatus status);

    /**
     * 오래된 PENDING 데이터 조회 (재처리용)
     * 특정 시간 이전에 크롤링되었으나 아직 처리되지 않은 데이터를 조회합니다.
     * @param status 처리 상태 (보통 PENDING)
     * @param threshold 기준 시간
     * @return 오래된 PENDING 데이터 목록
     */
    @Query("SELECT r FROM RawCrawledData r WHERE r.status = :status AND r.crawledAt < :threshold ORDER BY r.crawledAt ASC")
    List<RawCrawledData> findOldPendingData(
        @Param("status") CrawlingStatus status,
        @Param("threshold") LocalDateTime threshold
    );

    /**
     * URL 존재 여부 확인
     * @param sourceUrl 소스 URL
     * @return 존재하면 true
     */
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 오늘 크롤링된 데이터 조회
     * @param startOfDay 오늘 00:00:00
     * @param endOfDay 오늘 23:59:59
     * @return 오늘 크롤링된 데이터 목록
     */
    @Query("SELECT r FROM RawCrawledData r WHERE r.crawledAt BETWEEN :startOfDay AND :endOfDay ORDER BY r.crawledAt DESC")
    List<RawCrawledData> findTodayCrawledData(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}
