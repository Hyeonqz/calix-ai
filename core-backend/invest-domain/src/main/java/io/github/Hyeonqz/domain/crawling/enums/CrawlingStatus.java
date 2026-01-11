package io.github.Hyeonqz.domain.crawling.enums;

/**
 * 크롤링 데이터 처리 상태
 */
public enum CrawlingStatus {
    PENDING("대기 중"),
    PROCESSING("처리 중"),
    PROCESSED("처리 완료"),
    FAILED("실패");

    private final String description;

    CrawlingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
