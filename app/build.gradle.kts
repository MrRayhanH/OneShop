plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    ("apply plugin: 'com.google.gms.google-services'")
}

android {
    namespace = "com.example.oneshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.oneshop"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.makeramen:roundedimageview:2.3.0")
    implementation ("com.github.mancj:MaterialSearchBar:0.8.5")
    ("implementation platform('com.google.firebase:firebase-bom:33.7.0')")
    ("implementation 'com.google.firebase:firebase-auth:23.1.0'")
}