plugins {
    kotlin("jvm") version "2.2.0-RC2"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.blueb"
version = "2025.5.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))

    implementation("org.spongepowered:configurate-core:4.2.0")
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    implementation("org.spongepowered:configurate-extra-kotlin:4.2.0")

    implementation("com.zaxxer:HikariCP:4.0.3")
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
        downloadPlugins {
            url("https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
        }
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
