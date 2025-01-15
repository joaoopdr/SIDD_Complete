plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.symptom_checker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.symptom_checker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "FLASK_API_KEY",
            "\"${project.findProperty("FLASK_API_KEY")}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        buildFeatures {
            buildConfig = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }

    sourceSets["main"].assets.srcDirs("src/main/assets")
}

val composeUiVersion = "1.4.3"

dependencies {
    // AndroidX and Compose
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.v143)
    implementation(libs.constraintlayout)
    implementation(libs.material.v190)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite.v2120)
    implementation(libs.tensorflow.lite.support.v043)
    implementation(libs.tensorflow.lite.gpu.v2120)

    // Google Play Services
    implementation(libs.play.services.maps.v1810)
    implementation(libs.play.services.location.v2101)

    // Networking
    implementation(libs.volley)
    implementation(libs.retrofit.v290)
    implementation(libs.converter.gson.v290)
    implementation(libs.gson)

    // Image Processing
    implementation(libs.image.labeling.v1707)
    implementation(libs.picasso.v28)

    // Maps
    implementation(libs.osmdroid.android.v6116)

    // Jitsi Meet
    implementation("org.jitsi.react:jitsi-meet-sdk:+") {
        isTransitive = true
    }

    // QR Code
    implementation(libs.google.core)
    implementation(libs.zxing.android.embedded)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling.v143)
    debugImplementation(libs.androidx.ui.test.manifest.v143)

    implementation(libs.material)

    // Rounded Image View
    implementation(libs.roundedimageview)

    //HTTP Request
    implementation(libs.okhttp)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Gson for JSON parsing
    implementation(libs.gson)

    // LiveData and ViewModel (optional but recommended for managing UI states)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}