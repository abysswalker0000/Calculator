plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.androidcalculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.androidcalculator"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android    x.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation ("com.google.firebase:firebase-messaging:24.3.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    implementation("org.mozilla:rhino:1.7R4")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}