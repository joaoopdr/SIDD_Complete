buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath(libs.gradle.v850)
        classpath(libs.kotlin.gradle.plugin.v1821)
        classpath(libs.google.services)
        classpath(libs.google.services.v442)

    }
    extra["compose_ui_version"] = "1.2.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://github.com/jitsi/jitsi-maven-repository/raw/master/releases")
        }
        maven {
            url = uri("https://www.jitpack.io")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject)
}
