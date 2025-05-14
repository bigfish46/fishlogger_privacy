// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

tasks.wrapper {
    gradleVersion = "8.11.1"
    distributionType = Wrapper.DistributionType.BIN
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}