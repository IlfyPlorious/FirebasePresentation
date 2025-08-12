plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.firebasedemo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.firebasedemo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // match your Compose version
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // ✅ Compose BOM (keeps versions in sync)
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.androidx.foundation.layout)

    debugImplementation("androidx.compose.ui:ui-tooling")

    // ✅ Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // ✅ Hilt + Hilt Navigation Compose
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ✅ Lifecycle + ViewModel + StateFlow support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")

    // ✅ Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Optional: Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Optional: Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    // Official Compose integration (experimental)
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.1")
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Retrofit Moshi converter
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Moshi JSON library
    implementation("com.squareup.moshi:moshi:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")  // for code gen (optional but recommended)

    // Chucker for network inspection (debug only)
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    implementation("com.squareup.moshi:moshi:1.15.0")

    // Moshi Kotlin support (this includes KotlinJsonAdapterFactory)
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Moshi codegen for annotation processing (optional but recommended)
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    // ... other dependencies
    implementation("androidx.camera:camera-camera2:1.3.1") // Use the latest version
    // You'll also likely need camera-lifecycle and camera-view
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    /**
     * Firebase dependencies
     */

    // Core
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Additional

    implementation("com.google.firebase:firebase-ml-modeldownloader")

    // Also add the dependency for the TensorFlow Lite library and specify its version
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    // helps with performance and op support
    // For task-specific libraries (e.g., image classification))

    // object detection kit
    implementation("com.google.mlkit:object-detection:17.0.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}