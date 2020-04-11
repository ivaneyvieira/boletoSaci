plugins {
  kotlin("jvm")
  //id("org.springframework.boot") version "2.2.6.RELEASE"
  // id("org.jetbrains.kotlin.plugin.spring") version "1.3.71"
}
//apply(plugin = "io.spring.dependency-management")
repositories {
  jcenter()
  maven(url = "http://maven.vaadin.com/vaadin-addons")
}
//springBoot {
//  mainClassName = "rest.ApplicationKt"
//}
dependencies {
  // implementation(project(":"))
  compile(kotlin("stdlib-jdk8"))
  compile("mysql:mysql-connector-java:5.1.47")
  implementation("org.springframework.boot:spring-boot-starter-web")
  //providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}