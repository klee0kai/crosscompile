plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


gradlePlugin {
    plugins.register("crosscompile_publish") {
        id = "maven_publish.crosscompile_publish"
        implementationClass = "maven_publish.CrossCompilePublishPlugin"
    }
}
