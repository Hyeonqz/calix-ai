val springBootAdminVersion by extra("3.5.7")
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
    implementation("org.springframework.boot:spring-boot-starter-cache") // Spring cache
    implementation("org.springframework.boot:spring-boot-starter-data-redis") // Spring Redis

    // admin-client
    implementation("de.codecentric:spring-boot-admin-starter-client")

    // Module Dependencies
    implementation(project(":invest-domain"))
    implementation(project(":module-shared"))

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.8")
}

// bootJar 활성화 (실행 가능한 애플리케이션)
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

tasks.named<Jar>("jar") {
    enabled = false
}
dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
    }
}
