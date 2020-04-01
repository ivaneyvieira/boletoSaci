package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.boletoSaci.model.arquivosBancario.BoletoExt
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.framework.util.mid
import br.com.caelum.stella.boleto.Boleto
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto
import java.io.FileInputStream
import java.nio.file.Paths

class BoletoSaci(val listDadosPagador: List<DadosPagador>, val dadosConvenio: DadosConvenio) {
  fun buildBoleto(contrato: DadosPagador, nossoNumero: Int) = Boleto.novoBoleto()
    .comDatas(contrato.buildDatas())
    .comBanco(dadosConvenio.banco)
    .comBeneficiario(dadosConvenio.buildBeneficiario(nossoNumero))
    .comInstrucoes(* dadosConvenio.instrucoes)
    .comLocaisDePagamento(* dadosConvenio.locaisPagamento)
    .comPagador(contrato.buildPagador())
    .comValorBoleto(contrato.valorParcela)
    .comEspecieDocumento("DMI")
    .comNumeroDoDocumento(contrato.numeroDocumento)
  
  fun buildGerador(): GeradorDeBoleto {
    val boletos = buildListBoleto().map {it.boleto}
    return GeradorDeBoleto(template(), mapOf("digitoNossoNumero" to ""), * boletos.toTypedArray())
  }
  
  fun buildListBoleto(): List<BoletoExt> {
    return listDadosPagador.map {dadosPagador ->
      buildBoleto(dadosPagador)
    }
  }
  
  fun buildBoleto(dadosPagador: DadosPagador): BoletoExt {
    val nossoNumero = updateBoleto(dadosPagador)
    val boleto = buildBoleto(dadosPagador, nossoNumero)
    return BoletoExt(boleto, dadosPagador.chaveERP)
  }
  
  fun updateBoleto(dadosPagador: DadosPagador): Int {
    return when {
      dadosPagador.boletoEmitido -> dadosPagador.nossoNumero
      else                       -> dadosPagador.updateNossoNumero()
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
