plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "fun.nekomc"
version = "2.0-alpha.1"

repositories {
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://nexus.hc.to/content/repositories/pub_releases/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    mavenCentral()
}

dependencies {
    api(rootProject.libs.cn.hutool.hutool.core)
    compileOnly(rootProject.libs.org.spigotmc.spigot.api)
    compileOnly(rootProject.libs.com.google.guava.guava.collections)
    testImplementation(rootProject.libs.org.spigotmc.spigot.api)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

sourceSets {
    test {
        java {
            srcDir("src/main/java")
            srcDir("src/test/java")
        }
    }
}

apply {
    plugin("com.github.johnrengelman.shadow")
}

tasks.shadowJar {
    archiveBaseName.set(project.name)
    archiveClassifier.set("plugin")
    archiveVersion.set(project.version.toString())

    // 重定向 hutool 包
    relocate("cn.hutool", "fun.nekomc.sw.libs.cn.hutool")

    // 构建后执行，移动 libs 文件夹下全部文件到 jar-app/plugins 目录下
    doLast {
        val libsDir = File(project.layout.buildDirectory.get().asFile, "libs")
        val pluginsDir = File(project.parent?.layout?.projectDirectory?.asFile, "jar-app/plugins")
        if (libsDir.exists()) {
            libsDir.listFiles()?.forEach {
                it.copyTo(File(pluginsDir, it.name), true)
            }
        }
    }
}

tasks.withType<JavaCompile> {
    // 编码
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}