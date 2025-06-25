import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {

    dependencies {
        classpath ("com.google.gms:google-services:4.4.1")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}

plugins {
    id ("com.android.application") version "8.10.1" apply false
    id ("com.android.library") version "8.10.1" apply false
    id ("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id ("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id ("com.google.dagger.hilt.android") version "2.51.1" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.3.15" apply false
    id ("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            // Using a provider - though your current method should be fine
            jvmTarget.set(project.provider { org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11 })
        }
    }
}


//allprojects {
//    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
//        kotlinOptions {
//            jvmTarget = "11"
//        }
//    }
//}

