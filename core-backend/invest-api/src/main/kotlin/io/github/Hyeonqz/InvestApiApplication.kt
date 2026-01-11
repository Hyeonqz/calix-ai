package io.github.Hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.github.Hyeonqz"])
class InvestApiApplication

fun main(args: Array<String>) {
    runApplication<InvestApiApplication>(*args)
}
