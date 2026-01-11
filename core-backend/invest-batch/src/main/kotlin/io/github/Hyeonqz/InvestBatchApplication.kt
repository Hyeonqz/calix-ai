package io.github.Hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["io.github.Hyeonqz"])
class InvestBatchApplication

fun main(args: Array<String>) {
    runApplication<InvestBatchApplication>(*args)
}
