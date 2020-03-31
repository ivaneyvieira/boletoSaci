package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.framework.view.ConverteByte
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession

class ViewBoletoHelp {
  companion object {
    fun showBoleto(contratos: List<DadosPagador>) {
      val dadosConvenio = DadosConvenio.CONVENIO_ITAU
      val boleto = BoletoSaci(contratos, dadosConvenio)
      val pdfBoleto = boleto.geraBoleto()
      //val dialog = SubWindowPDF(pdfBoleto)
      //dialog.open()
      val timeNumber = System.currentTimeMillis()
      val resourcePDF = StreamResource("$timeNumber.pdf", ConverteByte(pdfBoleto))
 
      val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resourcePDF)
      UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    }
  }
}