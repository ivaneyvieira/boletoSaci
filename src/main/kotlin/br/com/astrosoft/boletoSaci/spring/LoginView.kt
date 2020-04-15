package br.com.astrosoft.boletoSaci.spring

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@Route("login")
@PageTitle("Login | Vaadin CRM")
class LoginView: VerticalLayout(), BeforeEnterObserver {
  @Autowired
  lateinit var loginInfo: LoginInfo
  
  @Autowired
  lateinit var loginFormApp: LoginFormApp
  
  override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
    if(isError(beforeEnterEvent))
      loginFormApp.isError = true
  }
  
  private fun isError(beforeEnterEvent: BeforeEnterEvent): Boolean {
    return beforeEnterEvent.location
      .queryParameters
      .parameters
      .getOrDefault("error", emptyList())
      .isNotEmpty()
  }
  
  init {
    addClassName("login-view")
    setSizeFull()
    justifyContentMode = CENTER
    alignItems = Alignment.CENTER
    loginFormApp.action = "login"
    add(H1(loginInfo.appName), loginFormApp)
  }
}