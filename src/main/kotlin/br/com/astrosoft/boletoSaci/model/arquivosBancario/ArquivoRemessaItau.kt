package br.com.astrosoft.boletoSaci.model.arquivosBancario

import br.com.astrosoft.boletoSaci.model.DadosBeneficiario
import br.com.astrosoft.boletoSaci.model.DadosBeneficiario.Companion.DADOS_BENEFICIARIO
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.DadosConvenio.Companion.CONVENIO_ITAU
import br.com.caelum.stella.boleto.Boleto
import br.com.caelum.stella.boleto.bancos.Bancos
import java.time.LocalDate
import java.time.LocalDateTime

class ArquivoRemessaItau: Arquivo<HeaderRetorno, DetailRetorno, TrailerRetorno>() {
  init {
    header {
      number(1, HeaderRetorno::tipoRegistro)
      number(1, HeaderRetorno::operacao)
      alpha(7, HeaderRetorno::literalRemessa)
      number(2, HeaderRetorno::codigoServico)
      alpha(15, HeaderRetorno::literalServico)
      number(4, HeaderRetorno::agencia)
      number(2, HeaderRetorno::zeros)
      number(5, HeaderRetorno::conta)
      number(1, HeaderRetorno::dac)
      alpha(8, HeaderRetorno::brancos1)
      alpha(30, HeaderRetorno::nomeEmpresa)
      number(3, HeaderRetorno::codigoBanco)
      alpha(15, HeaderRetorno::nomeBanco)
      date(HeaderRetorno::dataGeracao)
      alpha(294, HeaderRetorno::brancos2)
      number(6, HeaderRetorno::numeroSequancial)
    }
    
    detail {
      number(1, DetailRetorno::tipoRegistro)
      number(2, DetailRetorno::codigoInscricao)
      number(14, DetailRetorno::numeroInscricao)
      
      number(4, DetailRetorno::agencia)
      number(2, DetailRetorno::zeros)
      number(5, DetailRetorno::conta)
      
      number(1, DetailRetorno::dac)
      alpha(4, DetailRetorno::brancos1)
      number(4, DetailRetorno::instrucaoAlegacao)
      
      alpha(25, DetailRetorno::usoEmpresa)
      number(8, DetailRetorno::nossoNumero)
      quant(13, DetailRetorno::qtdeMoeda)
      
      number(3, DetailRetorno::numeroCarteira)
      alpha(21, DetailRetorno::usoBanco)
      alpha(1, DetailRetorno::carteira)
      
      number(2, DetailRetorno::codigoOcorrencia)
      alpha(10, DetailRetorno::numeroDocumento)
      date(DetailRetorno::dataVencimento)
      
      money(13, DetailRetorno::valorTitulo)
      number(3, DetailRetorno::codigoBanco)
      number(5, DetailRetorno::agenciaCobradora)
      
      alpha(2, DetailRetorno::especie)
      alpha(1, DetailRetorno::aceite)
      date(DetailRetorno::dataEmissao)
      
      alpha(2, DetailRetorno::instrucao1)
      alpha(2, DetailRetorno::instrucao2)
      money(13, DetailRetorno::juros1Dia)
      
      date(DetailRetorno::descontoAte)
      money(13, DetailRetorno::valorDesconto)
      money(13, DetailRetorno::valorIOF)
      
      money(13, DetailRetorno::abatimento)
      number(2, DetailRetorno::codigoInscricaoPagador)
      number(14, DetailRetorno::numeroInscricaoPagador)
      
      alpha(30, DetailRetorno::nome)
      alpha(10, DetailRetorno::brancos2)
      alpha(40, DetailRetorno::lougadouro)
      
      alpha(12, DetailRetorno::bairro)
      number(8, DetailRetorno::cep)
      alpha(15, DetailRetorno::cidade)
      
      alpha(2, DetailRetorno::estado)
      alpha(30, DetailRetorno::nomeSacadorAvalista)
      alpha(4, DetailRetorno::brancos3)
      
      date(DetailRetorno::dataMora)
      number(2, DetailRetorno::prazo)
      alpha(1, DetailRetorno::brancos4)
      
      number(6, DetailRetorno::numeroSequancial)
    }
    
    trailer {
      number(1, TrailerRetorno::tipoRegistro)
      alpha(393, TrailerRetorno::brancos)
      number(6, TrailerRetorno::numeroSequancial)
    }
  }
  
  fun buildFile(boletos: List<Boleto>): List<String> {
    var sequencia = 0L
    val linhas = mutableListOf<String>()
    val boleto = boletos.firstOrNull() ?: return emptyList()
    linhas += header.line(HeaderRetorno(sequencia++))
    boletos.forEach {b ->
      linhas += detail.line(DetailRetorno(sequencia++, b, ""))
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
  val nomeBanco: String = banco.nomeDoBanco
  val dataGeracao: LocalDate = LocalDate.now()
  val brancos2: String = ""
  
  //
  val numeroSequancial = sequencia
}

class DetailRetorno(private val sequencia: Long, private val boleto: Boleto, chaveERP: String) {
  private val banco: Bancos = Bancos.ITAU
  private val beneficiario: DadosBeneficiario = DADOS_BENEFICIARIO!!
  private val convenio: DadosConvenio = CONVENIO_ITAU
  val tipoRegistro = 1L
  val codigoInscricao = 10L
  val numeroInscricao =
    beneficiario.documento.replace("/", "")
      .replace("-", "")
      .replace(".", "")
      .toLongOrNull() ?: 0L
  val agencia = convenio.agencia.toLongOrNull() ?: 0L
  val zeros = 0L
  val conta = convenio.codigo.toLongOrNull() ?: 0L
  val dac = convenio.digitoCodigo.toLongOrNull() ?: 0L
  val brancos1: String = ""
  val instrucaoAlegacao: Long = 0L
  val usoEmpresa: String = chaveERP
  val nossoNumero =
    banco.banco.getNossoNumeroFormatado(boleto.beneficiario)
      .toLongOrNull() ?: 0L
  val qtdeMoeda = 0.00
  val numeroCarteira = convenio.carteira.toLongOrNull() ?: 0L
  val usoBanco: String = ""
  val carteira: String = "I"
  val codigoOcorrencia = 1L
  val numeroDocumento: String = boleto.numeroDoDocumento
  val dataVencimento: LocalDate =
    LocalDateTime.ofInstant(boleto.datas.vencimento.toInstant(),
                            boleto.datas.vencimento.timeZone
                              .toZoneId())
      .toLocalDate()
  val valorTitulo: Double = boleto.valorBoleto.toDouble()
  val codigoBanco = banco.numeroDoBanco.toLongOrNull() ?: 0L
  val agenciaCobradora = 0L
  val especie: String = "01"
  val aceite: String = if(boleto.aceite) "S" else "N"
  val dataEmissao: LocalDate = LocalDateTime.ofInstant(boleto.datas.processamento.toInstant(),
                                                       boleto.datas.processamento.timeZone
                                                         .toZoneId())
    .toLocalDate()
  val instrucao1: String = "03"
  val instrucao2: String = "03"
  val juros1Dia: Double = 0.00
  val descontoAte: LocalDate = LocalDateTime.ofInstant(boleto.datas.vencimento.toInstant(),
                                                       boleto.datas.vencimento.timeZone
                                                         .toZoneId())
    .toLocalDate()
  val valorDesconto: Double = 0.00
  val valorIOF: Double = 0.00
  val abatimento: Double = 0.00
  val codigoInscricaoPagador = 1L
  val numeroInscricaoPagador = boleto.pagador.documento.toLongOrNull() ?: 0L
  val nome: String = boleto.pagador.nome
  val brancos2: String = ""
  val lougadouro: String = boleto.pagador.endereco.logradouro
  val bairro: String = boleto.pagador.endereco.bairro
  val cep = boleto.pagador.endereco.cep.toLongOrNull() ?: 0L
  val cidade: String = boleto.pagador.endereco.cidade
  val estado: String = boleto.pagador.endereco.uf
  val nomeSacadorAvalista: String = ""
  val brancos3: String = ""
  val dataMora: LocalDate? = null
  val prazo = 30L
  val brancos4: String = ""
  val numeroSequancial = sequencia
}

class TrailerRetorno(private val sequencia: Long) {
  val tipoRegistro = 9L
  val brancos: String = ""
  val numeroSequancial = sequencia
}