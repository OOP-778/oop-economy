plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

val inteliFrameworkVersion = "1.0"

group = "me.oop.economy"
version = "1.0-SNAPSHOT"

subprojects {
    apply {
        plugin("java")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        jcenter()
        mavenLocal()
        maven { setUrl("https://repo.codemc.org/repository/nms/") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.8")
        annotationProcessor("org.projectlombok:lombok:1.18.8")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

