import org.gradle.kotlin.dsl.testImplementation

plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.2.0.6526"
    jacoco
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "modul4-menejemen-pengiriman-sawit"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}


dependencies {
    // API & Web - PAKAI INI supaya databind/Jackson masuk
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Lombok - Tambahkan testAnnotationProcessor
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Dev Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ... (Bagian plugins sampai dependencies tetap sama) ...

tasks.withType<Test> {
    useJUnitPlatform()
    // Agar setiap kali kamu run 'test', dia otomatis buat laporan JaCoCo
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    // Pastikan test jalan dulu sebelum bikin report
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        // Set lokasi output agar SonarCloud tidak bingung mencari
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
    }
}

sonar {
    properties {
        property("sonar.projectKey", "advprog-2026-A18-project_Manajemen-Pengiriman-Hasil-Panen-Sawit")
        property("sonar.organization", "advprog-2026-a18-project")
        property("sonar.host.url", "https://sonarcloud.io")
        // Gunakan path dinamis agar selalu akurat di environment mana pun
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

// Tambahkan ini agar saat kamu jalankan './gradlew sonar', dia otomatis ngerjain test dulu
tasks.named("sonar") {
    dependsOn(tasks.jacocoTestReport)
}

