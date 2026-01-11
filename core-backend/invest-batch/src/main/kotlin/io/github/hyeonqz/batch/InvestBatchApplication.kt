package io.github.hyeonqz.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["io.github.hyeonqz"])
@EntityScan("io.github.hyeonqz.domain")
@EnableJpaRepositories("io.github.hyeonqz.domain")
class InvestBatchApplication

fun main(args: Array<String>) {
    runApplication<InvestBatchApplication>(*args)
}
