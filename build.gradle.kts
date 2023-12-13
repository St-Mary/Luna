plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
    `maven-publish`
}

group = "com.stmarygate"
version = "1.0.0"

repositories {
    mavenCentral()

    kotlin.run {
        val envFile = file(".env")
        envFile.readLines().forEach {
            if (it.isNotEmpty() && !it.startsWith("#")) {
                val pos = it.indexOf("=")
                val key = it.substring(0, pos)
                val value = it.substring(pos + 1)
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value)
                }
            }
        }
    }

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/St-Mary/CommonLib")
        credentials {
            username = System.getProperty("GITHUB_ACTOR").toString()
            password = System.getProperty("GITHUB_TOKEN").toString()
        }
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

// Required by the 'shadowJar' task
project.setProperty("mainClassName", "com.stmarygate.gameserver.Luna")

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.reflections", "reflections", "0.10.2")
    implementation("io.github.cdimascio", "java-dotenv", "5.1.1")
    implementation("ch.qos.logback", "logback-classic", "1.2.9")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.netty:netty-all:4.1.101.Final")

    // Database
    implementation("org.postgresql:postgresql:42.7.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.hibernate.orm:hibernate-core:6.3.0.CR1")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.3.0.CR1")
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    // implementation("com.stmarygate.common:stmarygate-commonlib:1.0.5")
    implementation(files("/Users/noelle/Desktop/Developpement/Projets/StMary-Gate/stmarygate-commonlib/build/libs/stmarygate-commonlib-1.0.6.dev.2.jar"))
}

tasks {
    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.stmarygate.gameserver.Luna"
    }

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("stmary-gameserver")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.test {
    useJUnitPlatform()
}