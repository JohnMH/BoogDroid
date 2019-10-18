plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applicationId ="ws.lamm.bugdroid"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "0.0.4"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    useLibrary("org.apache.http.legacy")
}

dependencies {
    implementation(Libraries.kotlinStdLib)

    implementation(Libraries.appCompat)
    implementation(Libraries.recycleView)
    implementation(Libraries.design)
    implementation(Libraries.axmlrpc)
    implementation(Libraries.gson)
    implementation(Libraries.activeandroid)

    testImplementation(TestLibraries.junitJupiterApi)
    testImplementation(TestLibraries.junitJupiterParams)
    testRuntimeOnly(TestLibraries.junitJupiterEngine)

    testImplementation(TestLibraries.kotlinTestCommon)
    testImplementation(TestLibraries.kotlinTestAnnotationsCommon)
    testImplementation(TestLibraries.kotlinTestJunit5)
    testImplementation(TestLibraries.assertjCore)
}
