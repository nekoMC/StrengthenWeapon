plugins {
    id("java")
}

group = "fun.nekomc"
version = "1.0-beta.4"

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
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<JavaCompile> {
    // 编码
    options.encoding = "UTF-8"
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

tasks.test {
    useJUnitPlatform()
}