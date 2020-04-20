package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.boletoSaci.model.arquivosBancario.BoletoExt
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.framework.util.mid
import br.com.caelum.stella.boleto.Boleto
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto
import java.io.FileInputStream
import java.nio.file.Paths

class BoletoSaci(val listDadosPagador: List<DadosBoleto>, val dadosConvenio: DadosConvenio) {
  fun buildBoleto(dadosBoleto: DadosBoleto, nossoNumero: Int) = Boleto.novoBoleto()
    .comDatas(dadosBoleto.buildDatas())
    .comBanco(dadosConvenio.banco)
    .comBeneficiario(dadosConvenio.buildBeneficiario(nossoNumero))
    .comInstrucoes(* dadosConvenio.instrucoes(dadosBoleto.chaveERP))
    .comLocaisDePagamento(* dadosConvenio.locaisPagamento)
    .comPagador(dadosBoleto.buildPagador())
    .comValorBoleto(dadosBoleto.valorTotal)
    .comEspecieDocumento("DMI")
    .comNumeroDoDocumento(dadosBoleto.numeroDocumento)
  
  fun buildGerador(): GeradorDeBoleto {
    val boletos = buildListBoleto().map {it.boleto}
    return GeradorDeBoleto(template(), mapOf("digitoNossoNumero" to ""), * boletos.toTypedArray())
  }
  
  fun buildListBoleto(): List<BoletoExt> {
    return listDadosPagador.map {dadosBoleto ->
      buildBoleto(dadosBoleto)
    }
  }
  
  fun buildBoleto(dadosBoleto: DadosBoleto): BoletoExt {
    val nossoNumero = updateBoleto(dadosBoleto)
    val boleto = buildBoleto(dadosBoleto, nossoNumero)
    return BoletoExt(boleto, dadosBoleto.chaveERP)
  }
  
  fun updateBoleto(dadosBoleto: DadosBoleto): Int {
    return when {
      dadosBoleto.boletoEmitido -> dadosBoleto.nossoNumero
      else                      -> dadosBoleto.updateNossoNumero()
    }
  }
  
  private fun template(): FileInputStream {
    val arquivo = "/report/Relatorio/boleto-sem-sacador-avalista.jasper"
    val resource = SystemUtils::class.java.getResource(arquivo)
    val path = Paths.get(resource.toURI())
    return FileInputStream(path.toFile())
  }
  
  fun geraBoleto() = buildGerador().geraPDF()
}

fun String?.formataCep(): String? {
  this ?: return ""
  val parte1 = if(this.length >= 5) this.mid(0, 5) else ""
  val parte2 = if(this.length >= 8) this.mid(5, 3) else ""
  if(parte1 == "") return ""
  if(parte2 == "") return "$parte1-000"
  return "$parte1-$parte2"
}
