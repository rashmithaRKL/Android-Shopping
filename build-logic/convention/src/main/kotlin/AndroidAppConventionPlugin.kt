import com.razzaghi.shopingbykmp.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.razzaghi.shopingbykmp.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidApplication").get().get().pluginId)
            apply(libs.findPlugin("kotlin.serialization").get().get().pluginId)
            apply(libs.findPlugin("kotlin.parcelize").get().get().pluginId)
        }

        extensions.configure<ApplicationExtension> {
            namespace = "com.razzaghi.shopingbykmp.android"
            compileSdk = libs.findVersion("android.compileSdk").get().toString().toInt()

            defaultConfig {
                applicationId = "com.razzaghi.shopingbykmp.android"
                minSdk = libs.findVersion("android.minSdk").get().toString().toInt()
                targetSdk = libs.findVersion("android.targetSdk").get().toString().toInt()
                versionCode = libs.findVersion("android.version.code").get().toString().toInt()
                versionName = libs.findVersion("android.version.name").get().toString()
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }

            compileOptions {
                sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
            }
        }

        extensions.configure<KotlinMultiplatformExtension> {
            androidTarget()
        }
    }
}
