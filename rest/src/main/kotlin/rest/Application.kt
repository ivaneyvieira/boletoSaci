package rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application

fun main(args: Array<String>) {
  val app = SpringApplication(Application::class.java)
 //app.webApplicationType = NONE
  app.run(*args)
}

