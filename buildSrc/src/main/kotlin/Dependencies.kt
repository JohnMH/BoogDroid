const val kotlinVersion = "1.3.50"

object BuildPlugins {
    object Versions {
        const val buildToolsVersion = "3.5.1"
        const val androidJunit5 = "1.5.2.0"
        const val objectboxVersion = "2.4.0"
    }

    const val androidApplication = "com.android.application"
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val objectboxGradlePlugin = "io.objectbox:objectbox-gradle-plugin:${Versions.objectboxVersion}"
    const val androidJunit5 = "de.mannodermaus.gradle.plugins:android-junit5:${Versions.androidJunit5}"

}

object AndroidSdk {
    const val min = 27
    const val compile = 29
    const val target = 27
}

object Libraries {
    private object Versions {
        const val activeandroid = "3.1.0-SNAPSHOT"
        const val jetpack = "27.1.1"
        const val kodein = "6.4.1"
    }

    object AndroidSupport {
        const val appcompat = "com.android.support:appcompat-v7:${Versions.jetpack}"
        const val design = "com.android.support:design:${Versions.jetpack}"
        const val recycleview =  "com.android.support:recyclerview-v7:${Versions.jetpack}"
    }

    object KodeinDI {
        const val genericJvm = "org.kodein.di:kodein-di-generic-jvm:${Versions.kodein}"
        const val frameworkAndroidCore = "org.kodein.di:kodein-di-framework-android-core:${Versions.kodein}"
        const val frameworkAndroidSupport = "org.kodein.di:kodein-di-framework-android-support:${Versions.kodein}"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val axmlrpc = "com.github.gturri:axmlrpc:master"
    const val activeandroid = "com.michaelpardo:activeandroid:${Versions.activeandroid}"
}

object TestLibraries {
    private object Versions {
        const val assertj = "3.13.2"
        const val junit = "5.5.2"
    }

    object KotlinTest {
        const val common = "org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion"
        const val annotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion"
        const val junit5 ="org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion"
    }

    object JunitJupiter {
        const val api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
        const val params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit}"
        const val engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    }

    const val assertjCore = "org.assertj:assertj-core:${Versions.assertj}"
}
