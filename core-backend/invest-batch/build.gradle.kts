val springBootAdminVersion by extra("3.5.7")

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web") // Web (required for actuator endpoints)
    implementation("org.springframework.boot:spring-boot-starter-batch") // batch
    implementation("org.springframework.boot:spring-boot-starter-quartz")  // quartz
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
    implementation("org.springframework.boot:spring-boot-starter-cache") // Spring cache
    implementation("org.springframework.boot:spring-boot-starter-data-redis") // Spring Redis
    implementation("org.springframework.boot:spring-boot-starter-actuator") // Spring Actuator
    implementation("de.codecentric:spring-boot-admin-starter-client") // Admin Client

    // Module Dependencies
    implementation(project(":invest-domain"))
    implementation(project(":module-shared"))

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
    }
}

// bootJar 활성화 (실행 가능한 애플리케이션)
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

tasks.named<Jar>("jar") {
    enabled = false
}