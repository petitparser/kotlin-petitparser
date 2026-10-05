plugins {
  kotlin("multiplatform") version "2.4.20"
  id("maven-publish")
  jacoco
}

group = "com.github.petitparser"
version = "1.2.0"

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
    commonTest.dependencies {
      implementation(kotlin("test"))
    }
  }
}

tasks.register<JacocoReport>("jacocoTestReport") {
  dependsOn("jvmTest")
  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(true)
  }
  classDirectories.setFrom(layout.buildDirectory.dir("classes/kotlin/jvm/main"))
  sourceDirectories.setFrom(files("src/commonMain/kotlin"))
  executionData.setFrom(layout.buildDirectory.file("jacoco/jvmTest.exec"))
}

tasks.named("check") {
  dependsOn("jacocoTestReport")
}