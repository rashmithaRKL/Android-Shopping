plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    implementation("com.android.tools.build:gradle:8.1.0")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.5.11")
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "com.razzaghi.shopingbykmp.kotlinMultiplatform"
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
        register("shared") {
            id = "com.razzaghi.shopingbykmp.shared"
            implementationClass = "SharedConventionPlugin"
        }
        register("androidApp") {
            id = "com.razzaghi.shopingbykmp.androidApp"
            implementationClass = "AndroidAppConventionPlugin"
        }
    }
}
