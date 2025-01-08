plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("org.telegram:telegrambots-longpolling:7.10.0")
    implementation ("org.telegram:telegrambots-client:7.10.0")
    implementation ("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.slf4j:slf4j-nop:2.0.7")

    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.hibernate.orm:hibernate-core:7.0.0.Beta3")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

    implementation("org.apache.logging.log4j:log4j-core:2.24.0")
    implementation("org.apache.logging.log4j:log4j-api:2.24.0")
}

tasks.test {
    useJUnitPlatform()
}