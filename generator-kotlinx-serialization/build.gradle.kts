plugins {
    alias(idofrontLibs.plugins.mia.kotlin.multiplatform)
    alias(idofrontLibs.plugins.mia.publication)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        browser()
        nodejs()
    }
    wasmJs() {
        browser()
        nodejs()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(idofrontLibs.kotlinx.serialization.json)
            implementation(idofrontLibs.kotlinx.serialization.kaml)
            implementation(project(":dsl"))
            implementation(project(":annotations"))
        }
        all {
            languageSettings.enableLanguageFeature("ContextParameters")
        }
    }
}