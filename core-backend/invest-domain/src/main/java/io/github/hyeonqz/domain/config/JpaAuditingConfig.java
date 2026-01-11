package io.github.hyeonqz.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정
 * BaseTimeEntity의 @CreatedDate, @LastModifiedDate 자동 처리를 활성화합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
