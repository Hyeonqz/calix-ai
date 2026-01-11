package io.github.Hyeonqz.domain.crawling.entity;

import io.github.Hyeonqz.domain.common.BaseTimeEntity;
import io.github.Hyeonqz.domain.crawling.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 크롤링 작업의 실행 이력을 관리하는 Entity
 * 하루 1회 실행되는 배치 작업의 성공/실패를 추적합니다.
 */
@Entity
@Table(
    name = "crawling_job",
    indexes = {
        @Index(name = "idx_job_name", columnList = "job_name"),
        @Index(name = "idx_started_at", columnList = "started_at"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrawlingJob extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작업 이름 (예: "DAILY_NEWS_CRAWLING")
     */
    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    /**
     * 크롤링 대상 URL
     */
    @Column(name = "target_url", length = 2048)
    private String targetUrl;

    /**
     * 작업 실행 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    /**
     * 작업 시작 일시
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * 작업 완료 일시
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 전체 처리 건수
     */
    @Column(name = "total_count")
    private Integer totalCount;

    /**
     * 성공 건수
     */
    @Column(name = "success_count")
    private Integer successCount;

    /**
     * 실패 건수
     */
    @Column(name = "fail_count")
    private Integer failCount;

    /**
     * 실패 시 오류 메시지
     */
    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 작업을 성공 상태로 완료 처리
     * @param successCount 성공 건수
     * @param failCount 실패 건수
     */
    public void complete(int successCount, int failCount) {
        this.status = JobStatus.SUCCESS;
        this.completedAt = LocalDateTime.now();
        this.successCount = successCount;
        this.failCount = failCount;
        this.totalCount = successCount + failCount;
    }

    /**
     * 작업을 실패 상태로 처리
     * @param errorMessage 오류 메시지
     */
    public void fail(String errorMessage) {
        this.status = JobStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    /**
     * 작업이 실행 중인지 확인
     * @return RUNNING 상태이면 true
     */
    public boolean isRunning() {
        return this.status == JobStatus.RUNNING;
    }

    /**
     * 작업이 성공했는지 확인
     * @return SUCCESS 상태이면 true
     */
    public boolean isSuccess() {
        return this.status == JobStatus.SUCCESS;
    }

    /**
     * 작업 실행 시간 계산 (초 단위)
     * @return 작업 실행 시간 (초), 아직 완료되지 않았으면 null
     */
    public Long getExecutionTimeInSeconds() {
        if (completedAt == null) {
            return null;
        }
        return java.time.Duration.between(startedAt, completedAt).getSeconds();
    }
}
