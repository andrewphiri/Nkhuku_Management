
plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp")
    id ("kotlin-parcelize")
    id ("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
    id ("com.google.protobuf") version "0.9.4"
    id ("kotlin-kapt")
    id ("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.0.21"
}
android {
    namespace = "and.drew.nkhukumanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "and.drew.nkhukumanagement"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "and.drew.nkhukumanagement.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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



//    kotlin {
//        jvmToolchain(11)
//    }

    // Allow references to generated code
    kapt {
        correctErrorTypes = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }


    testOptions {
        animationsDisabled = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {
//    val composeUiVersion = "1.5.3"
//    val navVersion = "2.7.7"
//    val roomVersion = "2.6.1"
//    val workVersion = "2.9.0"
//    val lifecycleVersion = "2.7.0"

    implementation ("androidx.navigation:navigation-compose:2.9.0")
    implementation ("androidx.core:core-ktx:1.16.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation ("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation ("androidx.compose.material3:material3-window-size-class:1.3.2")
    implementation ("androidx.compose.material:material-icons-extended")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation ("androidx.test:core-ktx:1.6.1")
    implementation ("androidx.appcompat:appcompat:1.7.1")
    implementation ("androidx.test:rules:1.6.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-storage:21.0.2")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4")
    debugImplementation ("androidx.compose.ui:ui-tooling")
    debugImplementation ("androidx.compose.ui:ui-test-manifest")
    debugImplementation ("androidx.test:monitor:1.7.2")
    androidTestImplementation ("androidx.test:runner:1.6.1")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.room:room-runtime:2.7.2")

    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:2.7.2")
    implementation ("androidx.room:room-ktx:2.7.2")

    //Hilt
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation ("com.google.dagger:hilt-android:2.51.1")
    kapt ("com.google.dagger:hilt-compiler:2.51.1")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.0")

    //Hilt WorkManager
    implementation ("androidx.hilt:hilt-work:1.2.0")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    //Test Kotlin
    testImplementation("com.google.dagger:hilt-android-compiler:2.48")
    kaptTest("com.google.dagger:hilt-android-compiler:2.48")
    //For instrumented tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    //....with kotlin
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")

    // WorkManager Kotlin + coroutines
    implementation ("androidx.work:work-runtime-ktx:2.10.2")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")

    //Credential Manager
    implementation ("androidx.credentials:credentials:1.5.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore-ktx")


    // Add the dependencies for the Firebase Cloud Messaging and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")

    //Apache Poi for exporting room as excel
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    //Coil Library
    implementation("io.coil-kt:coil-compose:2.7.0")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    // Lifecycle utilities for Compose
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    //Proto DataStore
    implementation("androidx.datastore:datastore:1.1.7")
    //Protobuf
    implementation ("com.google.protobuf:protobuf-javalite:3.25.5")
    //Navigation Test
    androidTestImplementation ("androidx.navigation:navigation-testing:2.9.0")
    //Test rules and transitive dependencies
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.8.3")
    //Needed for createAndroidComposeRule, but not createComposeRule
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.5.3")

    //Billing Client
    implementation("com.android.billingclient:billing-ktx:7.1.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.1"
    }
    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    plugins {
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") {
                        option("lite")
                    }
                }
            }
        }
    }
}
