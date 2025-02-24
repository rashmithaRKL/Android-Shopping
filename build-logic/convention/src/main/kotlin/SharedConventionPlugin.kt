import com.razzaghi.shopingbykmp.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.razzaghi.shopingbykmp.configureKotlinAndroid
import com.android.build.api.dsl.LibraryExtension

class SharedConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidLibrary").get().get().pluginId)
            apply(libs.findPlugin("kotlin.serialization").get().get().pluginId)
        }

        extensions.configure<LibraryExtension>(::configureKotlinAndroid)

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(libs.findLibrary("kotlinx.serialization.json").get())
                        implementation(libs.findLibrary("kotlinx.datetime").get())
                        implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                    }
                }

                androidMain {
                    dependencies {
                        implementation(libs.findLibrary("androidx.activity.compose").get())
                        implementation(libs.findLibrary("androidx.appcompat").get())
                        implementation(libs.findLibrary("androidx.core").get())
                        implementation(libs.findLibrary("koin.android").get())
                        implementation(libs.findLibrary("ktor.okhttp").get())
                    }
                }
            }
        }
    }
}
