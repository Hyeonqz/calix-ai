package io.github.hyeonqz.domain.crawling.entity;

import io.github.hyeonqz.domain.common.BaseTimeEntity;
import io.github.hyeonqz.domain.crawling.enums.CrawlingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 크롤링한 원본 데이터를 저장하는 Entity
 * ETL 파이프라인의 Extract(추출) 단계에서 생성됩니다.
 */
@Entity
@Table(
    name = "raw_crawled_data",
    indexes = {
        @Index(name = "idx_source_url", columnList = "source_url"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_crawled_at", columnList = "crawled_at")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RawCrawledData extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 크롤링 소스 URL
     */
    @Column(name = "source_url", nullable = false, length = 255)
    private String sourceUrl;

    /**
     * 페이지 제목
     */
    @Column(name = "title", length = 500)
    private String title;

    /**
     * 원본 컨텐츠 (HTML, JSON 등)
     * LONGTEXT 타입으로 최대 4GB까지 저장 가능
     */
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 컨텐츠 타입 (HTML, JSON, XML 등)
     */
    @Column(name = "content_type", length = 50)
    private String contentType;

    /**
     * 처리 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CrawlingStatus status;

    /**
     * 크롤링 일시
     */
    @Column(name = "crawled_at", nullable = false)
    private LocalDateTime crawledAt;

    /**
     * 처리 완료 일시
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * 실패 시 오류 메시지
     */
    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 데이터를 처리 완료 상태로 변경
     */
    public void markAsProcessed() {
        this.status = CrawlingStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 데이터를 실패 상태로 변경
     * @param errorMessage 오류 메시지
     */
    public void markAsFailed(String errorMessage) {
        this.status = CrawlingStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * 처리 시작 상태로 변경
     */
    public void markAsProcessing() {
        this.status = CrawlingStatus.PROCESSING;
    }

    /**
     * 대기 중 상태인지 확인
     * @return PENDING 상태이면 true
     */
    public boolean isPending() {
        return this.status == CrawlingStatus.PENDING;
    }

    /**
     * 처리 완료 상태인지 확인
     * @return PROCESSED 상태이면 true
     */
    public boolean isProcessed() {
        return this.status == CrawlingStatus.PROCESSED;
    }
}
