plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.assignment1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.assignment1"
        minSdk = 24
        targetSdk = 35
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.robolectric:robolectric:4.10.3")  // hoặc phiên bản bạn mong muốn
    implementation ("com.google.android.material:material:1.8.0")
    testImplementation ("com.google.android.material:material:1.8.0") // ensure it's available at test time
    testImplementation("junit:junit:4.12")
    testImplementation("org.easytesting:fest:1.0.16")
    testImplementation("org.robolectric:robolectric:3.1.4")
    testImplementation("org.khronos:opengl-api:gl1.1-android-2.1_r1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}