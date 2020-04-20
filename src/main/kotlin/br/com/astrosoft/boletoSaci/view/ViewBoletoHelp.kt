package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.arquivosBancario.ArquivoRemessaItau
import br.com.astrosoft.boletoSaci.model.mail.TemplateMail
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.model.Gmail
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.view.ConverteByte
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDate

class ViewBoletoHelp {
  companion object {
    private val dadosConvenio = DadosConvenio.CONVENIO_ITAU
  
    fun gravaArquivoBoleto(numLote: Int, dadosBoletos: List<DadosBoleto>) {
      dadosBoletos.sortedBy {it.codigo}
        .groupBy {it.codigo}
        .forEach {(codigo, dados) ->
          arquivoBoleto(numLote, dados.firstOrNull()?.codigo, dados.firstOrNull()?.nome)?.let {nomeDoArquivo ->
            println("Gravando arquivo $nomeDoArquivo ...")
            gravaArquivoBoleto(nomeDoArquivo, dados.sortedWith(compareBy ({it.storeno}, {it.contrno}, {it.instno})))
          }
        }
    }
  
    private fun arquivoBoleto(numLote: Int, codigo: Int?, nome: String?): String? {
      codigo ?: return null
      nome ?: return null
      val dir = diretorioLote(numLote)
      val codigoStr = codigo.toString()
        .lpad(6, "0")
      return "$dir/$codigoStr - $nome.pdf"
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
  
    fun gravaArquivoRemessa(numLote: Int, dadosBoletos: List<DadosBoleto>) {
      val diretorioLote = diretorioLote(numLote)
      File(diretorioLote).mkdirs()
      val filename = "$diretorioLote/${arquivoRemessa(numLote)}"
      gravaArquivoRemessa(filename, dadosBoletos)
    }
  
    private fun diretorioLote(numLote: Int): String {
      val dir = "/home/ivaneyvieira/Insync/ivaney@pintos.com.br/Google Drive/boletoPintos"
      val loteStr =
        numLote.toString()
          .lpad(2, "0")
      return "$dir/lote$loteStr"
    }
  
    private fun arquivoRemessa(numLote: Int): String {
      val date = LocalDate.now()
      val ano = (date.year - 2020).toString()
      val mes =
        date.monthValue.toString()
          .lpad(2, "0")
      val dia =
        date.dayOfMonth.toString()
          .lpad(2, "0")
      val lote =
        numLote.toString()
          .lpad(2, "0")
      return "a$ano$mes$dia$lote.txt"
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
  
    fun enviarEmail(numLote: Int, codigoCliente: Int) {
      val boletosCliente =
        saci.dadosBoletos(numLote)
          .filter {codigoCliente == it.codigo}
      val msgHtml = TemplateMail.corpoEmailHTML(codigoCliente, boletosCliente)
      val nome = boletosCliente.firstOrNull()?.nome
      val email = boletosCliente.firstOrNull()?.email ?: ""
    
      arquivoBoleto(numLote, codigoCliente, nome)?.let {arquivoPDF ->
        val gmail = Gmail()
        val result = gmail.sendMail(email, "Solicitação - Lojas Pintos", msgHtml, arquivoPDF)
        if(result)
          gravaLog("$codigoCliente\t$nome\t$arquivoPDF\t$email\t$numLote")
        else
          gravaLog("E$codigoCliente\t$nome\t$arquivoPDF\t$email\t$numLote")
      }
    }
  
    private fun gravaLog(logText: String) {
      val arquivo = "/home/ivaneyvieira/logEmail.txt"
      Files.write(Paths.get(arquivo), "$logText\n".toByteArray(), StandardOpenOption.APPEND)
    }
  
    fun codigosEnviados(): List<Int> {
      val arquivo = "/home/ivaneyvieira/logEmail.txt"
      return File(arquivo).bufferedReader()
        .readLines()
        .mapNotNull {linha ->
          linha.split("\t")
            .getOrNull(0)
            ?.toIntOrNull()
        }
        .distinct()
    }
  }
}