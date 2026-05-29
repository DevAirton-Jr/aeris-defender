plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.sentinelav"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Testes
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// CONFIGURAÇÃO DO JAVAFX
javafx {
    version = "21.0.2"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}

// MAIN DA APLICAÇÃO
application {
    mainClass.set("com.sentinelav.ui.AppLauncher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
