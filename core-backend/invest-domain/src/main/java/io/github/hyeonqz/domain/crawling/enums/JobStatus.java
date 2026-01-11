package io.github.hyeonqz.domain.crawling.enums;

/**
 * 크롤링 작업 실행 상태
 */
public enum JobStatus {
    RUNNING("실행 중"),
    SUCCESS("성공"),
    FAILED("실패");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
