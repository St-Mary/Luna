plugins {
    id("java")
    application
    `maven-publish`
    kotlin("jvm")
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
        url = uri("https://maven.pkg.github.com/St-Mary/Coral")
        credentials {
            username = System.getProperty("GITHUB_ACTOR").toString()
            password = System.getProperty("GITHUB_TOKEN").toString()
        }
    }
}

configurations {
    all {
        exclude(group = "org.slf4j", module = "slf4j-logback")
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
project.setProperty("mainClassName", "com.stmarygate.luna.Luna")

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.reflections", "reflections", "0.10.2")
    implementation("io.github.cdimascio", "java-dotenv", "5.1.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.netty:netty-all:4.1.101.Final")

    // Database
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.hibernate.orm:hibernate-core:6.4.1.Final")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.4.1.Final")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    //implementation("com.stmarygate:coral:1.0.17")
    implementation(files("/Users/noelle/Desktop/Developpement/Projets/StMary-Gate/coral/build/libs/coral-1.0.18.jar"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}