val springBootAdminVersion by extra("3.5.7")

plugins {
    id("java")
}

group = "io.github.hyeonqz"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // springboot web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // spring admin-server
    implementation("de.codecentric:spring-boot-admin-starter-server")

    // test
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")
}
tasks.test {
    useJUnitPlatform()
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
    }
}
