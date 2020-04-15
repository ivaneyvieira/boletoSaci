package br.com.astrosoft.boletoSaci.spring

import br.com.astrosoft.framework.view.ViewUtil
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class Application: SpringBootServletInitializer() {
  private val versao = ViewUtil.versao
  private val title = "Boleto Crediário"
  private val shortName = "Boleto"
  private val iconPath = "icons/logoPintos.png"
  
  @Bean
  fun loginInfo() = LoginInfo(title, shortName, versao, iconPath)
  //@Bean
  //fun loginFormApp() = LoginFormApp()
}

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}

open class LoginInfo(val appName: String, val shortName: String, val version: String, val iconPath: String)