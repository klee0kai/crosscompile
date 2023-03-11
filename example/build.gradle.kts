plugins {
    id("java")
    kotlin("jvm")
    application
    id("com.github.klee0kai.androidnative")
}




dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}