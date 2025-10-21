plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.traveler"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.traveler"
        minSdk = 30
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
    aaptOptions {
        noCompress("tflite")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.navigation.compose)

    // Using the latest version for ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Use Firebase BoM to manage versions
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Add individual Firebase libraries. The BoM sets the version for you. (IOT)
    implementation("com.google.firebase:firebase-database-ktx")

    // Firebase Authentication for email/password sign-in and Google Sign-In
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // Google Sign-In specific library
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Cloud Firestore for the chat part
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Charting library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


    // ai model integration part
    // implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.3")
    // Add this line to include the TFLite core interpreter
    implementation("org.tensorflow:tensorflow-lite:2.15.0")
    // CRITICAL FIX: Add the select-tf-ops library to support custom/newer ops
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.15.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.3")

    
    //for data store
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("io.coil-kt:coil-compose:2.5.0") // Use the latest version

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}