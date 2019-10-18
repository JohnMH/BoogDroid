const val kotlinVersion = "1.3.50"

object BuildPlugins {
    object Versions {
        const val buildToolsVersion = "3.5.1"
        const val androidJunit5 = "1.5.2.0"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val androidJunit5 = "de.mannodermaus.gradle.plugins:android-junit5:${Versions.androidJunit5}"
}

object AndroidSdk {
    const val min = 27
    const val compile = 27
    const val target = compile
}

object Libraries {
    private object Versions {
        const val jetpack = "27.1.1"
        const val gson = "2.8.5"
        const val activeandroid = "3.1.0-SNAPSHOT"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val appCompat = "com.android.support:appcompat-v7:${Versions.jetpack}"
    const val design = "com.android.support:design:${Versions.jetpack}"
    const val recycleView =  "com.android.support:recyclerview-v7:${Versions.jetpack}"
    const val axmlrpc = "com.github.gturri:axmlrpc:master"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val activeandroid = "com.michaelpardo:activeandroid:${Versions.activeandroid}"
}

object TestLibraries {
    private object Versions {
        const val junit = "5.5.2"
        const val assertj = "3.13.2"
    }

    const val kotlinTestCommon = "org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion"
    const val kotlinTestAnnotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion"
    const val kotlinTestJunit5 ="org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion"

    const val junitJupiterApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
    const val junitJupiterParams = "org.junit.jupiter:junit-jupiter-params:${Versions.junit}"
    const val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"

    const val assertjCore = "org.assertj:assertj-core:${Versions.assertj}"
}
