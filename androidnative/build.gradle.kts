plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


gradlePlugin {
    plugins.register("androidnative") {
        id = "com.github.klee0kai.androidnative"
        implementationClass = "com.github.klee0kai.androidnative.AndroidNativePlugin"
    }
}
