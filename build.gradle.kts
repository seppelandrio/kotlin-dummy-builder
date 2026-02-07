plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinter)
}

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
}

tasks {
    test {
        useJUnitPlatform()
    }
}
