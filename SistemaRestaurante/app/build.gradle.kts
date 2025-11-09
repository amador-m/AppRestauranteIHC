plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.sistemarestaurante"
    compileSdk = 36 // (Ou a sua versão, ex: 34)

    defaultConfig {
        applicationId = "com.example.sistemarestaurante"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // DEPENDÊNCIAS DO FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(libs.firebase.auth.ktx)
    // implementation(libs.firebase.firestore.ktx) // (Você não estava usando, mas pode manter se quiser)

    // --- LINHA REMOVIDA ---
    // implementation("com.google.firebase:firebase-messaging-ktx")

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)

    // Glide (Imagens)
    implementation(libs.glide)
    kapt(libs.glide.kapt) // (Use 'annotationProcessor' se 'kapt' não funcionar)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}