package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.arquivosBancario.ArquivoRemessaItau
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.view.ConverteByte
import br.com.astrosoft.framework.view.resource.resourceTxt
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ViewBoletoHelp {
  companion object {
    private val dadosConvenio = DadosConvenio.CONVENIO_ITAU
  
    fun showBoleto(dadosBoletos: List<DadosBoleto>) {
      val dir = "/tmp/"
      dadosBoletos.sortedBy {it.codigo}
        .groupBy {it.codigo}
        .forEach {(codigo, dados) ->
          val codigoStr =
            codigo.toString()
              .lpad(6, "0")
          val nomeClinete = dados.firstOrNull()?.nome ?: "SEM NOME"
          val nomeDoArquivo = "$dir/$codigoStr - $nomeClinete.pdf"
          println("Gravando arquivo $nomeDoArquivo ...")
          gravaArquivoBoleto(nomeDoArquivo, dados.sortedBy {it.nossoNumero})
        }
    }
  
    private fun gravaArquivoBoleto(filename: String, dadosBoletos: List<DadosBoleto>) {
      val boleto = BoletoSaci(dadosBoletos, dadosConvenio)
      val pdfBoleto = boleto.geraBoleto()
      val file = File(filename)
    
      try {
        val os: OutputStream = FileOutputStream(file)
        os.write(pdfBoleto)
        os.close()
      } catch(e: Exception) {
        e.printStackTrace()
      }
    }
  
    fun showBoletoBrowser(dadosBoletos: List<DadosBoleto>) {
      val boleto = BoletoSaci(dadosBoletos, dadosConvenio)
      val pdfBoleto = boleto.geraBoleto()
      val timeNumber = System.currentTimeMillis()
      val resourcePDF = StreamResource("$timeNumber.pdf", ConverteByte(pdfBoleto))
      val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resourcePDF)
      UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    }
  
    fun showArquivoRemessa(numLote: Int, dadosBoletos: List<DadosBoleto>) {
      val diretorioLote = diretorioLote(numLote)
      File(diretorioLote).mkdirs()
    }
  
    private fun diretorioLote(numLote: Int): String {
      val dir = "/home/ivaneyvieira/Insync/ivaney@pintos.com.br/Google Drive - Shared with me/boletosPintos/"
      val loteStr =
        numLote.toString()
          .lpad(2, "0")
      return "$dir/lote$loteStr"
    }
  
    fun gravaArquivoRemessa(filename: String, dadosBoletos: List<DadosBoleto>) {
      val arquivoRemessa = ArquivoRemessaItau()
      val boletosSaci = BoletoSaci(dadosBoletos, dadosConvenio)
      val boletos = boletosSaci.buildListBoleto()
      val arquivo = arquivoRemessa.buildFile(boletos)
      val arquivoStr = arquivo.joinToString(separator = "\r\n", postfix = "\r\n")
      val file = File(filename)
    
      try {
        val os: OutputStream = FileOutputStream(file)
        os.write(arquivoStr.toByteArray())
        os.close()
      } catch(e: Exception) {
        e.printStackTrace()
      }
    }
  
    fun showArquivoRemessaBrowser(dadosBoletos: List<DadosBoleto>) {
      val arquivoRemessa = ArquivoRemessaItau()
      val boletosSaci = BoletoSaci(dadosBoletos, dadosConvenio)
      val boletos = boletosSaci.buildListBoleto()
      val arquivo = arquivoRemessa.buildFile(boletos)
      val arquivoStr = arquivo.joinToString(separator = "\r\n", postfix = "\r\n")
      val resource = resourceTxt(arquivoStr)
      val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)
      UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    }
  }
}