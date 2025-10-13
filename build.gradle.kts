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

val ktorVersion = "2.3.7"

dependencies {
	testImplementation(kotlin("test"))

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

	implementation("io.ktor:ktor-server-core:${ktorVersion}")
	implementation("io.ktor:ktor-server-netty:${ktorVersion}")

	implementation("io.ktor:ktor-server-cors:${ktorVersion}")
	implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
	implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
	implementation("io.ktor:ktor-server-partial-content:${ktorVersion}")
	implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(22)
}