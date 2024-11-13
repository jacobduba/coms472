plugins {
    id("java")
    application
}

group = "edu.iastate.cs472.proj2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("edu.iastate.cs472.proj2.Checkers")
}

tasks.run.configure {
    standardInput = System.`in`
}
