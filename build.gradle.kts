plugins {
	kotlin("jvm") version "2.1.21"
	kotlin("plugin.serialization") version "2.1.21"

	id("io.ktor.plugin") version "3.2.3"
}

group = "org.lrc-server"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

application {
    mainClass = "com.lrc.server.MainKt"
}

val ktorVersion = "2.3.7"
val logbackVersion = "1.4.11"
val koinKtorVersion = "4.1.1"
val exposedVersion = "1.0.0-rc-1"
val h2Version = "2.2.224"

dependencies {
	testImplementation(kotlin("test"))

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

	implementation("io.ktor:ktor-server-core:${ktorVersion}")
	implementation("io.ktor:ktor-server-netty:${ktorVersion}")

    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("io.insert-koin:koin-ktor:$koinKtorVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinKtorVersion")

    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")

	implementation("io.ktor:ktor-server-cors:${ktorVersion}")
	implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
	implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
	implementation("io.ktor:ktor-server-partial-content:${ktorVersion}")
	implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-migration-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:$exposedVersion")


    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("com.h2database:h2:2.2.224")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(22)
}