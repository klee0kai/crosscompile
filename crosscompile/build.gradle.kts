plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


gradlePlugin {
    plugins.register("crosscompile") {
        id = "crosscompile"
        group = "com.github.klee0kai"
        version = "0.0.1"
        implementationClass = "com.github.klee0kai.crosscompile.CrossCompilePlugin"
        displayName = "Crosscompile"
        description = "Gradle plugin for native libs building"
    }
}


dependencies {
    implementation(gradleApi())

    // https://mvnrepository.com/artifact/com.android.tools.build/builder
    implementation("com.android.tools.build:builder:7.4.2")

    // https://mvnrepository.com/artifact/com.android.tools.build/gradle
    implementation("com.android.tools.build:gradle:7.4.2")

}