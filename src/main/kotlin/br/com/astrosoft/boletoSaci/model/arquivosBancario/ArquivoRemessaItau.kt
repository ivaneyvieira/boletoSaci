package br.com.astrosoft.boletoSaci.model.arquivosBancario

import br.com.astrosoft.boletoSaci.model.DadosBeneficiario
import br.com.astrosoft.boletoSaci.model.DadosBeneficiario.Companion.DADOS_BENEFICIARIO
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.DadosConvenio.Companion.CONVENIO_ITAU
import br.com.astrosoft.framework.util.toLocalDate
import br.com.caelum.stella.boleto.Boleto
import br.com.caelum.stella.boleto.Pagador
import br.com.caelum.stella.boleto.bancos.Bancos
import java.time.LocalDate

class ArquivoRemessaItau: Arquivo<HeaderRetorno, DetailRetorno, TrailerRetorno>() {
  init {
    header {
      number(1, 1, HeaderRetorno::tipoRegistro)
      number(2, 1, HeaderRetorno::operacao)
      alpha(3, 7, HeaderRetorno::literalRemessa)
      //
      number(10, 2, HeaderRetorno::codigoServico)
      alpha(12, 15, HeaderRetorno::literalServico)
      number(27, 4, HeaderRetorno::agencia)
      //
      number(31, 2, HeaderRetorno::zeros)
      number(33, 5, HeaderRetorno::conta)
      number(38, 1, HeaderRetorno::dac)
      //
      alpha(39, 8, HeaderRetorno::brancos1)
      alpha(47, 30, HeaderRetorno::nomeEmpresa)
      number(77, 3, HeaderRetorno::codigoBanco)
      //
      alpha(80, 15, HeaderRetorno::nomeBanco)
      date(95, HeaderRetorno::dataGeracao)
      alpha(101, 294, HeaderRetorno::brancos2)
      //
      number(395, 6, HeaderRetorno::numeroSequancial)
    }
    
    detail {
      number(1, 1, DetailRetorno::tipoRegistro)
      number(2, 2, DetailRetorno::codigoInscricao)
      number(4, 14, DetailRetorno::numeroInscricao)
      //
      number(18, 4, DetailRetorno::agencia)
      number(22, 2, DetailRetorno::zeros)
      number(24, 5, DetailRetorno::conta)
      //
      number(29, 1, DetailRetorno::dac)
      alpha(30, 4, DetailRetorno::brancos1)
      number(34, 4, DetailRetorno::instrucaoAlegacao)
      //
      alpha(38, 25, DetailRetorno::usoEmpresa)
      number(63, 8, DetailRetorno::nossoNumero)
      quant(71, 13, DetailRetorno::qtdeMoeda)
      //
      number(84, 3, DetailRetorno::numeroCarteira)
      alpha(87, 21, DetailRetorno::usoBanco)
      alpha(108, 1, DetailRetorno::carteira)
      //
      number(109, 2, DetailRetorno::codigoOcorrencia)
      alpha(111, 10, DetailRetorno::numeroDocumento)
      date(121, DetailRetorno::dataVencimento)
      //
      money(127, 13, DetailRetorno::valorTitulo)
      number(140, 3, DetailRetorno::codigoBanco)
      number(143, 5, DetailRetorno::agenciaCobradora)
      //
      alpha(148, 2, DetailRetorno::especie)
      alpha(150, 1, DetailRetorno::aceite)
      date(151, DetailRetorno::dataEmissao)
      //
      alpha(157, 2, DetailRetorno::instrucao1)
      alpha(159, 2, DetailRetorno::instrucao2)
      money(161, 13, DetailRetorno::juros1Dia)
      //
      date(174, DetailRetorno::descontoAte)
      money(180, 13, DetailRetorno::valorDesconto)
      money(193, 13, DetailRetorno::valorIOF)
      //
      money(206, 13, DetailRetorno::abatimento)
      number(219, 2, DetailRetorno::codigoInscricaoPagador)
      number(221, 14, DetailRetorno::numeroInscricaoPagador)
      //
      alpha(235, 30, DetailRetorno::nome)
      alpha(265, 10, DetailRetorno::brancos2)
      alpha(275, 40, DetailRetorno::lougadouro)
      //
      alpha(315, 12, DetailRetorno::bairro)
      number(327, 8, DetailRetorno::cep)
      alpha(335, 15, DetailRetorno::cidade)
      //
      alpha(350, 2, DetailRetorno::estado)
      alpha(352, 30, DetailRetorno::nomeSacadorAvalista)
      alpha(382, 4, DetailRetorno::brancos3)
      //
      date(386, DetailRetorno::dataMora)
      number(392, 2, DetailRetorno::prazo)
      alpha(394, 1, DetailRetorno::brancos4)
      //
      number(395, 6, DetailRetorno::numeroSequancial)
    }
    
    trailer {
      number(1, 1, TrailerRetorno::tipoRegistro)
      alpha(2, 393, TrailerRetorno::brancos)
      number(395, 6, TrailerRetorno::numeroSequancial)
    }
  }
  
  fun buildFile(boletos: List<BoletoExt>): List<String> {
    var sequencia = 1L
    val linhas = mutableListOf<String>()
    
    linhas += header.line(HeaderRetorno(sequencia++))
    boletos.forEach {ext ->
      linhas += detail.line(DetailRetorno(sequencia++, ext.boleto, ext.chaveERP))
    }
    linhas += trailer.line(TrailerRetorno(sequencia))
    return linhas
  }
}

class HeaderRetorno(private val sequencia: Long) {
  private val banco: Bancos = Bancos.ITAU
  private val beneficiario: DadosBeneficiario = DADOS_BENEFICIARIO!!
  private val convenio: DadosConvenio = CONVENIO_ITAU
  
  //
  val tipoRegistro = 0L
  val operacao = 1L
  val literalRemessa: String = "REMESSA"
  
  //
  val codigoServico = 1L
  val literalServico: String = "COBRANCA"
  val agencia = convenio.agencia.toLongOrNull() ?: 0L
  
  //
  val zeros = 0L
  val conta = convenio.codigo.toLongOrNull() ?: 0L
  val dac = convenio.digitoCodigo.toLongOrNull() ?: 0L
  
  //
  val brancos1: String = ""
  val nomeEmpresa: String = beneficiario.nome
  val codigoBanco = banco.numeroDoBanco.toLongOrNull() ?: 0L
  
  //
  val nomeBanco: String = "ITAU"
  val dataGeracao: LocalDate = LocalDate.now()
  val brancos2: String = ""
  
  //
  val numeroSequancial = sequencia
}

class DetailRetorno(private val sequencia: Long, private val boleto: Boleto, chaveERP: String) {
  private val banco: Bancos = Bancos.ITAU
  private val beneficiario: DadosBeneficiario = DADOS_BENEFICIARIO!!
  private val pagador: Pagador = boleto.pagador
  private val convenio: DadosConvenio = CONVENIO_ITAU
  val tipoRegistro = 1L
  val codigoInscricao = 2L
  val numeroInscricao = beneficiario.documento.toNumber()
  val agencia = convenio.agencia.toNumber()
  val zeros = 0L
  val conta = convenio.codigo.toNumber()
  val dac = convenio.digitoCodigo.toNumber()
  val brancos1: String = ""
  val instrucaoAlegacao: Long = 0L
  val usoEmpresa: String = chaveERP
  val nossoNumero =
    banco.banco.getNossoNumeroFormatado(boleto.beneficiario)
      .toNumber()
  val qtdeMoeda = 0.00
  val numeroCarteira = convenio.carteira.toNumber()
  val usoBanco: String = ""
  val carteira: String = "I"
  val codigoOcorrencia = 1L
  val numeroDocumento: String = boleto.numeroDoDocumento
  val dataVencimento: LocalDate? = boleto.datas.vencimento.toLocalDate()
  val valorTitulo: Double = boleto.valorBoleto.toDouble()
  val codigoBanco = banco.numeroDoBanco.toNumber()
  val agenciaCobradora = 0L
  val especie: String = "01"
  val aceite: String = if(boleto.aceite) "S" else "N"
  val dataEmissao: LocalDate? = boleto.datas.processamento.toLocalDate()
  val instrucao1: String = "03"
  val instrucao2: String = "03"
  val juros1Dia: Double = convenio.jurosMensal.div(30.00)
  val descontoAte: LocalDate? = null
  val valorDesconto: Double = 0.00
  val valorIOF: Double = 0.00
  val abatimento: Double = 0.00
  val codigoInscricaoPagador = 1L
  val numeroInscricaoPagador = boleto.pagador.documento.toNumber()
  val nome: String = boleto.pagador.nome
  val brancos2: String = ""
  val lougadouro: String = boleto.pagador.endereco.logradouro
  val bairro: String = boleto.pagador.endereco.bairro
  val cep = boleto.pagador.endereco.cep.toNumber()
  val cidade: String = boleto.pagador.endereco.cidade
  val estado: String = boleto.pagador.endereco.uf
  val nomeSacadorAvalista: String = ""
  val brancos3: String = ""
  val dataMora: LocalDate? = null
  val prazo = 30L
  val brancos4: String = ""
  val numeroSequancial = sequencia
}

fun String.toNumber(): Long {
  val chars =
    this.toCharArray()
      .filter {it.isDigit()}
  return chars.joinToString("")
           .toLongOrNull() ?: 0L
}

class TrailerRetorno(private val sequencia: Long) {
  val tipoRegistro = 9L
  val brancos: String = ""
  val numeroSequancial = sequencia
}