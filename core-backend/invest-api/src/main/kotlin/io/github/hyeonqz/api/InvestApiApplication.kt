package io.github.hyeonqz.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["io.github.hyeonqz"])
@EntityScan("io.github.hyeonqz.domain")
@EnableJpaRepositories("io.github.hyeonqz.domain")
class InvestApiApplication

fun main(args: Array<String>) {
    runApplication<InvestApiApplication>(*args)
}
