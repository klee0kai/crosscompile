plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


gradlePlugin {
    plugins.register("androidnative") {
        id = "com.github.klee0kai.androidnative"
        implementationClass = "com.github.klee0kai.androidnative.CrossCompilePlugin"
    }
}


dependencies {
    implementation(gradleApi())

    // https://mvnrepository.com/artifact/com.android.tools.build/builder
    implementation("com.android.tools.build:builder:7.4.2")

    // https://mvnrepository.com/artifact/com.android.tools.build/gradle
    implementation("com.android.tools.build:gradle:7.4.2")

}