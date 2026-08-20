plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "lk.happypaws.app"
    compileSdk {
        version = release(37)
    }

    val apiBaseUrl: String = providers.gradleProperty("API_BASE_URL")
        .orElse(providers.environmentVariable("API_BASE_URL"))
        .getOrElse("https://10.233.202.121:7141/")

    val storageBaseUrl: String = providers.gradleProperty("STORAGE_BASE_URL")
        .orElse(providers.gradleProperty("R2_PUBLIC_URL"))
        .orElse(providers.environmentVariable("STORAGE_BASE_URL"))
        .orElse(providers.environmentVariable("R2_PUBLIC_URL"))
        .orElse(providers.environmentVariable("NEXT_PUBLIC_R2_PUBLIC_URL"))
        .getOrElse(
            run {
                if (apiBaseUrl.contains("10.") || apiBaseUrl.contains("192.168.") || apiBaseUrl.contains("172.") || apiBaseUrl.contains("localhost") || apiBaseUrl.contains("10.0.2.2")) {
                    val hostRegex = Regex("""https?://([^:/]+)""")
                    val match = hostRegex.find(apiBaseUrl)
                    val host = match?.groupValues?.get(1) ?: "10.233.202.121"
                    "http://$host:9000/happypaws-public"
                } else {
                    "https://cdn.happypaws.lk"
                }
            }
        )

    val customVersionCode: Int = providers.gradleProperty("versionCode")
        .map { it.toInt() }
        .getOrElse(1)

    val customVersionName: String = providers.gradleProperty("versionName")
        .getOrElse("1.0.0")

    val keystoreFilePath: String? = providers.gradleProperty("KEYSTORE_FILE")
        .orElse(providers.environmentVariable("KEYSTORE_FILE"))
        .orNull
    val keystorePassword: String? = providers.gradleProperty("KEYSTORE_PASSWORD")
        .orElse(providers.environmentVariable("KEYSTORE_PASSWORD"))
        .orNull
    val keyAlias: String? = providers.gradleProperty("KEY_ALIAS")
        .orElse(providers.environmentVariable("KEY_ALIAS"))
        .orNull
    val keyPassword: String? = providers.gradleProperty("KEY_PASSWORD")
        .orElse(providers.environmentVariable("KEY_PASSWORD"))
        .orNull

    signingConfigs {
        create("release") {
            if (!keystoreFilePath.isNullOrBlank() && file(keystoreFilePath).exists()) {
                storeFile = file(keystoreFilePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    defaultConfig {
        applicationId = "lk.happypaws.app"
        minSdk = 26
        targetSdk = 37
        versionCode = customVersionCode
        versionName = customVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "STORAGE_BASE_URL", "\"$storageBaseUrl\"")
    }

    buildTypes {
        release {
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning?.storeFile != null && releaseSigning.storeFile!!.exists()) {
                signingConfig = releaseSigning
            }
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.splashscreen)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // Image Loading
    implementation(libs.coil.compose)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
dependencies { 
    implementation("com.google.android.gms:play-services-location:21.0.1") 
}
dependencies { implementation("androidx.paging:paging-compose:3.3.5") }
