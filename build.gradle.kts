plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.cloud.tools.jib") version "3.4.4"
	jacoco
}

group = "io.jrb.labs"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

	testImplementation("app.cash.turbine:turbine:1.2.0")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
	testImplementation("com.ninja-squad:springmockk:3.1.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.apache.commons:commons-lang3:3.12.0")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}

jacoco {
	toolVersion = "0.8.12"
	reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

jib {
	from {
		image = "openjdk:21-jdk-slim"
		platforms {
			platform {
				architecture= "amd64"
				os = "linux"
			}
		}
	}
	to {
		image = "brulejr/${rootProject.name}"
		tags = setOf("latest", "$version")
	}
	container {
		ports = listOf("4100")
		creationTime = "USE_CURRENT_TIMESTAMP"
		jvmFlags = listOf("-Xms256m", "-Xmx512m", "-Dspring.config.additional-location=file:/config/")
	}
}