import java.io.File

plugins {
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

val localIdeaPath = providers.gradleProperty("localIdeaPath")
val runtimeJar = file("../jrebel-mybatisplus/target/jr-mybatisplus-1.0.8.jar")

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        if (localIdeaPath.isPresent) {
            local(file(localIdeaPath.get()))
            bundledPlugin("com.intellij.java")
        } else {
            intellijIdea("2026.2.0.1")
            bundledPlugin("com.intellij.java")
        }
        pluginVerifier()
    }
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(providers.gradleProperty("javaVersion").get())
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }
    instrumentCode = false
    pluginVerification {
        ides {
            // Local IDEA installation (e.g. 2026.3) when provided via gradle.properties
            if (localIdeaPath.isPresent) {
                local(file(localIdeaPath.get()))
            }
            // Verify against IDEA 2026.2 Ultimate as well (no local 2026.2 install)
            create("IU", "262.8665.337")
        }
    }
}

tasks {
    named("buildSearchableOptions") {
        enabled = false
    }
    named("prepareSandbox").configure {
        doLast {
            val pluginDir = outputs.files.firstOrNull { it.isDirectory && it.name == "jrebel-mybatisplus-idea-plugin" }
            if (pluginDir != null && runtimeJar.exists()) {
                val libDir = File(pluginDir, "lib")
                libDir.mkdirs()
                runtimeJar.copyTo(File(libDir, runtimeJar.name), overwrite = true)
                logger.lifecycle("Copied runtime jar to sandbox: ${File(libDir, runtimeJar.name).absolutePath}")
            } else {
                logger.warn("Sandbox plugin dir not found or runtime jar missing at $runtimeJar")
            }
        }
    }
    test {
        useJUnitPlatform()
    }
}
