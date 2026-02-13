plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.maven.publish)
}

group = "io.github.seppelandrio.kotlindummybuilder"

kotlin {
    jvmToolchain(
        libs.versions.jvm
            .get()
            .toInt(),
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.kotlinReflect)
    testImplementation(libs.jupiter.junitJupiter)
    testImplementation(libs.kotlin.kotlinTest)
    testRuntimeOnly(libs.jupiter.junitPlatformLauncher)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "kotlin-dummy-builder", version.toString())

    pom {
        name = "Kotlin Dummy Builder"
        description = "A lightweight Kotlin/ Java library for generating dummy objects based on reflection for testing and prototyping purposes."
        inceptionYear = "2026"
        url = "https://github.com/username/mylibrary/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "seppelandrio"
                name = "Sebastian Knapp"
                url = "https://github.com/seppelandrio/"
            }
        }
        scm {
            url = "https://github.com/seppelandrio/kotlin-dummy-builder"
            connection = "scm:git:git://github.com/seppelandrio/kotlin-dummy-builder.git"
            developerConnection = "scm:git:ssh://git@github.com/seppelandrio/kotlin-dummy-builder.git"
        }
    }
}
