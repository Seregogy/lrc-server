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
val exposed = "1.0.0-rc-1"
val h2 = "2.2.224"

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

    implementation("org.jetbrains.exposed:exposed-core:${exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposed}")
    implementation("org.jetbrains.exposed:exposed-migration-core:$exposed")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:$exposed")


    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("com.h2database:h2:2.2.224")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(22)
}