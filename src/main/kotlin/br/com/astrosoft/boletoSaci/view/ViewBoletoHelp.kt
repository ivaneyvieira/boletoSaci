package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.arquivosBancario.ArquivoRemessaItau
import br.com.astrosoft.framework.view.ConverteByte
import br.com.astrosoft.framework.view.resource.resourceTxt
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession

class ViewBoletoHelp {
  companion object {
    private val dadosConvenio = DadosConvenio.CONVENIO_ITAU
  
    fun showBoleto(dadosBoletos: List<DadosBoleto>) {
      val boleto = BoletoSaci(dadosBoletos, dadosConvenio)
      val pdfBoleto = boleto.geraBoleto()
      val timeNumber = System.currentTimeMillis()
      val resourcePDF = StreamResource("$timeNumber.pdf", ConverteByte(pdfBoleto))
      val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resourcePDF)
      UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    }
  
    fun showArquivoRemessa(dadosBoletos: List<DadosBoleto>) {
      val arquivoRemessa = ArquivoRemessaItau()
      val boletosSaci = BoletoSaci(dadosBoletos, dadosConvenio)
      val boletos = boletosSaci.buildListBoleto()
      val arquivo = arquivoRemessa.buildFile(boletos)
      val arquivoStr = arquivo.joinToString(separator = "\r\n")
      val resource = resourceTxt(arquivoStr)
      val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)
      UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    }
  }
}