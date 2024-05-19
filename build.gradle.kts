plugins {
    `java-library`
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    mavenCentral()
}

group = "fun.nekomc.sw.plugin"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        testImplementation(rootProject.libs.org.mockito.mockito.core)
        testImplementation(rootProject.libs.org.mockito.mockito.inline)
        testImplementation(rootProject.libs.org.junit.jupiter.junit.jupiter.api)
        testImplementation(rootProject.libs.org.junit.jupiter.junit.jupiter.engine)
        testImplementation(rootProject.libs.org.junit.vintage.junit.vintage.engine)
        testImplementation(rootProject.libs.org.junit.platform.junit.platform.launcher)
        testImplementation(rootProject.libs.org.junit.platform.junit.platform.runner)
        testImplementation(rootProject.libs.org.junit.platform.junit.platform.suite.api)
        testImplementation(rootProject.libs.org.junit.platform.junit.platform.suite.engine)
        compileOnly(rootProject.libs.org.jetbrains.annotations)
        compileOnly(rootProject.libs.org.projectlombok.lombok)
        annotationProcessor(rootProject.libs.org.projectlombok.lombok)
        testImplementation(rootProject.libs.org.projectlombok.lombok)
        testAnnotationProcessor(rootProject.libs.org.projectlombok.lombok)
        compileOnly(rootProject.libs.org.slf4j.slf4j.log4j12)
        testImplementation(rootProject.libs.org.slf4j.slf4j.log4j12)
        // kotlin 仅构建
        compileOnly(kotlin("stdlib"))
    }
}

group = "fun.nekomc"
version = "2.1-alpha.1"
description = "StrengthWeapon"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}