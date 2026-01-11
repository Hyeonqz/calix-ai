package io.github.Hyeonqz.domain.crawling.repository;

import io.github.Hyeonqz.domain.crawling.entity.CrawlingJob;
import io.github.Hyeonqz.domain.crawling.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 크롤링 작업 이력 Repository
 */
@Repository
public interface CrawlingJobRepository extends JpaRepository<CrawlingJob, Long> {

    /**
     * 가장 최근 작업 조회
     * @return 가장 최근에 시작된 작업
     */
    Optional<CrawlingJob> findTopByOrderByStartedAtDesc();

    /**
     * 상태별 작업 목록 조회
     * @param status 작업 상태
     * @return 작업 목록
     */
    List<CrawlingJob> findByStatus(JobStatus status);

    /**
     * 특정 기간 내 작업 이력 조회
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 작업 목록
     */
    List<CrawlingJob> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 실행 중인 작업 존재 여부 확인 (중복 실행 방지용)
     * @return 실행 중인 작업이 있으면 true
     */
    @Query("SELECT COUNT(j) > 0 FROM CrawlingJob j WHERE j.status = 'RUNNING'")
    boolean existsRunningJob();

    /**
     * 특정 작업명으로 가장 최근 작업 조회
     * @param jobName 작업 이름
     * @return 가장 최근 작업
     */
    Optional<CrawlingJob> findTopByJobNameOrderByStartedAtDesc(String jobName);

    /**
     * 작업명과 상태로 작업 목록 조회
     * @param jobName 작업 이름
     * @param status 작업 상태
     * @return 작업 목록
     */
    List<CrawlingJob> findByJobNameAndStatus(String jobName, JobStatus status);

    /**
     * 오늘 실행된 작업 목록 조회
     * @param startOfDay 오늘 00:00:00
     * @param endOfDay 오늘 23:59:59
     * @return 오늘 실행된 작업 목록
     */
    @Query("SELECT j FROM CrawlingJob j WHERE j.startedAt BETWEEN :startOfDay AND :endOfDay ORDER BY j.startedAt DESC")
    List<CrawlingJob> findTodayJobs(LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * 성공한 작업의 평균 성공률 조회
     * @return 평균 성공률 (0.0 ~ 1.0)
     */
    @Query("SELECT AVG(CAST(j.successCount AS double) / NULLIF(j.totalCount, 0)) FROM CrawlingJob j WHERE j.status = 'SUCCESS' AND j.totalCount > 0")
    Optional<Double> getAverageSuccessRate();
}
