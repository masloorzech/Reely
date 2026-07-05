import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val tmdbAccessToken = localProperties.getProperty("TMDB_ACCESS_TOKEN") ?: ""

android {
    namespace = "com.example.reely"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.reely"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "TMDB_ACCESS_TOKEN",
            "\"$tmdbAccessToken\""
        )

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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    implementation("com.composables:icons-lucide-cmp:2.2.1")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.security:security-crypto:1.1.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation(libs.material)
    implementation("io.coil-kt.coil3:coil-compose:3.5.0-beta01")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.dagger:hilt-android:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    ksp("com.google.dagger:hilt-compiler:2.59.2")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0-beta01")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

hilt {
    enableAggregatingTask = true
}