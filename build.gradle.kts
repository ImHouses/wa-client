plugins {
    kotlin("jvm") version "1.9.23"
    `maven-publish`
}

group = "dev.jcasas.wa.client"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    //SLF4J
    implementation("org.slf4j:slf4j-api:2.0.13")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Retrofit (HTTP Requests)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            groupId = "dev.jcasas" // Replace with your group ID
            artifactId = "wa.client" // Replace with your artifact ID
            version = "0.0.1" // Replace with your version

            pom {
                name.set("WhatsApp API Client")
                description.set("A Whatsapp API client for sending or receiving messages.")
                url.set("https://github.com/OWNER/REPOSITORY")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("jcasas")
                        name.set("Juan Casas")
                        email.set("imhouses19@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/OWNER/REPOSITORY.git")
                    developerConnection.set("scm:git:ssh://github.com/OWNER/REPOSITORY.git")
                    url.set("https://github.com/OWNER/REPOSITORY")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}