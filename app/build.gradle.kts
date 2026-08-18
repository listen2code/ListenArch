plugins {
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

group = "com.listen"

android {
    namespace = "com.listen.arch"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.10.0")

    // Coroutines & Lifecycle ViewModel Flow
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room Database
    api("androidx.room:room-runtime:2.8.4")
    api("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // DataStore Preferences
    api("androidx.datastore:datastore-preferences:1.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}