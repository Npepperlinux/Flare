pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
dependencyResolutionManagement {
    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://androidx.dev/snapshots/builds/13617490/artifacts/repository")
    }
}

rootProject.name = "Flare"
include(":app")
include(":shared")
include(":shared:ui")
include(":shared:ui:component")
include(":desktopApp")
include(":server")
include(":shared:api")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
