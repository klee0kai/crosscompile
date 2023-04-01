buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.21")
    }
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


gradlePlugin {
    plugins.register("crosscompile") {
        id = "com.github.klee0kai.crosscompile"
        implementationClass = "com.github.klee0kai.crosscompile.CrossCompilePlugin"
    }
}


dependencies {
    implementation(gradleApi())

    // https://github.com/klee0kai/shlex
    implementation("com.github.klee0kai:shlex:0.0.1")


    // https://mvnrepository.com/artifact/com.android.tools.build/builder
    implementation("com.android.tools.build:builder:7.4.2")

    // https://mvnrepository.com/artifact/com.android.tools.build/gradle
    implementation("com.android.tools.build:gradle:7.4.2")

}