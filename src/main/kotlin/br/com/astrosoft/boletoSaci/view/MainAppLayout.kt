package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.framework.view.KAppLayoutLeftLayout
import br.com.astrosoft.framework.view.ViewUtil
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Theme(value = Lumo::class, variant = Lumo.DARK)
@Push
@PWA(name = "Boleto Crediário",
     shortName = "Boleto Saci",
     iconPath = "icons/logoPintos.png")
class MainAppLayout: KAppLayoutLeftLayout(), BeforeEnterObserver {
  val versao = ViewUtil.versao
  init {
    layout("Boleto Saci", "icons/logoPintos.png") {
      bar(versao)
      menu {
        section("Boleto") {
          itemMenu(ViewBoletos::class)
          itemMenu(ViewPesquisaParcelas::class)
        }
      }
    }
  }
  
  override fun beforeEnter(event: BeforeEnterEvent?) {
    if(event?.navigationTarget == ViewEmpty::class.java)
      event.forwardTo(ViewBoletos::class.java)
  }
}
