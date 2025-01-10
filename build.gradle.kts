import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.5"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "dev.arubiku"
version = "1.0.0"
description = "A Minecraft plugin for UHC points management"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    reobfJar {
        dependsOn("build")
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

bukkitPluginYaml {
    main = "dev.arubiku.uhcpoints.UHCPoints"
    apiVersion = "1.21"
    authors.add("ArubikU")
    depend.add("PlaceholderAPI")
    commands {
        register("uhcpoints") {
            description = "UHC Points main command"
            permission = "uhcpoints.use"
        }
    }
    permissions {
        register("uhcpoints.use") {
            description = "Allows use of UHC Points commands"
        }
        register("uhcpoints.bypass") {
            description = "Bypass UHC Points ranking"
        }
    }
}

