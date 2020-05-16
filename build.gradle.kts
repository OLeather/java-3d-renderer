plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testCompile("junit", "junit", "4.12")
    compile(group = "org.ejml", name = "ejml-all", version = "0.38")
    compile(group = "com.github.mokiat", name = "java-data-front", version = "v2.0.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}