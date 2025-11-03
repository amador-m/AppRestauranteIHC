plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}
android {
    namespace = "com.example.sistemarestaurante"
    compileSdk = 36
    buildFeatures {
        viewBinding = true
    }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- Dependências existentes de UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // DEPENDÊNCIAS DO FIREBASE E COROUTINES (ADICIONADAS)
    implementation("com.google.firebase:firebase-database-ktx")

    // Importa o Firebase BOM para gerenciar versões automaticamente (RNF8)
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication (RF1, RF2)
    implementation(libs.firebase.auth.ktx)

    // Cloud Firestore (Para salvar o perfil do usuario e futuramente o cardapio)
    implementation(libs.firebase.firestore.ktx)

    // Coroutines (Usado no FirebaseManager para operacoes assincronas)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)

    // Dependências de teste existentes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.glide)
    kapt(libs.glide.kapt)
}
