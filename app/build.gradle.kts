plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
    id("io.objectbox")
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

        sourceSets.getByName("main") {
            java.srcDir("src/main/kotlin")
        }

        sourceSets.getByName("test") {
            java.srcDir("src/test/kotlin")
        }

        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // TODO: REMOVE THIS AND FIX BROKEN STUFF
    lintOptions {
        isAbortOnError = false
    }

    useLibrary("org.apache.http.legacy")
}

dependencies {
    implementation(Libraries.kotlinStdLib)

    implementation(Libraries.AndroidSupport.appcompat)
    implementation(Libraries.AndroidSupport.design)
    implementation(Libraries.AndroidSupport.recycleview)
    implementation(Libraries.axmlrpc)

    implementation(Libraries.activeandroid)

    implementation(Libraries.KodeinDI.genericJvm)
    implementation(Libraries.KodeinDI.frameworkAndroidCore)
    implementation(Libraries.KodeinDI.frameworkAndroidSupport)

    testImplementation(TestLibraries.JunitJupiter.api)
    testImplementation(TestLibraries.JunitJupiter.params)
    testRuntimeOnly(TestLibraries.JunitJupiter.engine)

    testImplementation(TestLibraries.KotlinTest.common)
    testImplementation(TestLibraries.KotlinTest.annotationsCommon)
    testImplementation(TestLibraries.KotlinTest.junit5)

    testImplementation(TestLibraries.assertjCore)
}
