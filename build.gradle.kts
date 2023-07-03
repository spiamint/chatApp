plugins {
	java
	id("org.springframework.boot") version "2.7.11"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
//	id("com.github.node-gradle.node") version "3.4.0"	// node.js
}
//apply(plugin = "com.github.node-gradle.node")

//// node 정보 추가
//node {
//	var version = "18.16.0"
//	var npmVersion = "9.5.1"
//	var download = true
//	var nodeProjectDir = file("${projectDir}/node_modules")
//}

group = "chat"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.projectlombok:lombok:1.18.22")
    compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("io.github.flashvayne:chatgpt-spring-boot-starter:1.0.4")

	implementation("org.springframework:spring-messaging")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Object to byteArray, StringUtils.replace
	implementation("org.apache.commons:commons-lang3:3.0")

	// MultiKeyMap
	implementation("org.apache.commons:commons-collections4:4.4")

	// mysql
	runtimeOnly("com.mysql:mysql-connector-j")

	// Mybatis
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.2")

	// log4jdbc
	implementation ("org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4.1:1.16")

	// https://mvnrepository.com/artifact/org.mapstruct/mapstruct
	implementation("org.mapstruct:mapstruct:1.5.3.Final")

	// https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")

	//spring security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// 타임리프에서 스프링시큐리티의 문법이나 형식을 지원하는 확장팩 라이브러리
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")

	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")



}

tasks.withType<Test> {
	useJUnitPlatform()
}
