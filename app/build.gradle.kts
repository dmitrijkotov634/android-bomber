plugins {
    id("com.android.application")

    kotlin("plugin.serialization") version "1.9.0"
    kotlin("android")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.dm.bomber"

    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.dm.bomber"
        minSdk = 21
        targetSdk = 34
        versionCode = 78
        versionName = "1.27"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")

    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))

    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.work:work-runtime:2.8.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.material:material:1.11.0-alpha02")

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("jp.wasabeef:blurry:4.0.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.jetradarmobile:android-snowfall:1.2.1")
    implementation("com.github.mazenrashed:DotsIndicatorWithoutViewpager:1.0.0")
}