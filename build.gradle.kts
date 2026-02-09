plugins {
  kotlin("multiplatform") version "2.3.10"
  id("maven-publish")
}

group = "com.github.petitparser"
version = "1.1.0"

repositories {
  mavenCentral()
}

kotlin {
  applyDefaultHierarchyTemplate()

  jvm()
  js {
    browser()
    nodejs()
  }
  macosArm64()
  macosX64()
  linuxArm64()
  linuxX64()
  mingwX64()

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jvmMain by getting
    val jvmTest by getting
    val jsMain by getting
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
  }
}