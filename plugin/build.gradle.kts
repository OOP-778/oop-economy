// Version of inteli-framework
val ifVersion = "1.0"

dependencies {
    implementation("com.oop.inteliframework:event:${ifVersion}")
    implementation("com.oop.inteliframework:bukkit-event:${ifVersion}")
    implementation("com.oop.inteliframework:platform:${ifVersion}")
    implementation("com.oop.inteliframework:config:${ifVersion}")
    implementation("com.oop.inteliframework:bukkit-item:${ifVersion}")
    implementation("com.oop.inteliframework:task:${ifVersion}")
    implementation("com.oop.inteliframework:bukkit-task:${ifVersion}")
    implementation("com.oop.inteliframework:dependency-common:${ifVersion}")
    implementation("com.oop.inteliframework:config-node:${ifVersion}")
    implementation("com.oop.inteliframework:config-property:${ifVersion}")
    implementation("com.oop.inteliframework:config-file:${ifVersion}")
    implementation("com.oop.inteliframework:bukkit-nbt:${ifVersion}")
    implementation("com.oop.inteliframework:message:${ifVersion}")
    implementation("com.oop.inteliframework:message-config:${ifVersion}")
    implementation("com.oop.inteliframework:command:${ifVersion}")
    implementation("com.oop.inteliframework:bukkit-command:${ifVersion}")
    implementation("com.oop.datamodule:universal:2.1")
    implementation("com.eatthepath:fast-uuid:0.1")

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.oop.inteliframework:commons:${ifVersion}")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDirectory.set(file("../server/plugins"))
        archiveFileName.set("oop-economy.jar")
    }
}
