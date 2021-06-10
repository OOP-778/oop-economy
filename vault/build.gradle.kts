val ifVersion = "1.0"

dependencies {
    compileOnly(fileTree("lib"))

    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
    compileOnly(project(":plugin"))
    compileOnly("com.oop.datamodule:universal:2.1")
    compileOnly("com.oop.inteliframework:platform:${ifVersion}")

}


tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDirectory.set(file("../server/plugins"))
        archiveFileName.set("oop-economy-vault.jar")
    }
}
