package br.com.astrosoft.boletoSaci.model

import br.com.caelum.stella.boleto.Banco
import br.com.caelum.stella.boleto.Beneficiario
import br.com.caelum.stella.boleto.Endereco
import br.com.caelum.stella.boleto.bancos.Itau
import java.text.DecimalFormat

data class DadosConvenio(
  val agencia: String,
  val digitoAgencia: String,
  val codigo: String,
  val digitoCodigo: String,
  val numeroConvenio: String,
  val carteira: String,
  val endereco: Endereco,
  val digitoNossoNumero: String,
  val banco: Banco,
  val dadosBeneficiario: DadosBeneficiario,
  val jurosMensal: Double
                        ) {
  fun instrucoes(parcela: String) = arrayOf(
                                            "APÓS O VENCIMENTO COBRAR JUROS DE $jurosFormatado AO MES",
                                            "APÓS 30 DIAS DO VENCIMENTO CONTATE NOSSO SETOR",
                                            "DE COBRANÇA TELEFONE/WHATSAPP 86 2107-4000 ($parcela)"
                                           )
  
  private val jurosFormatado: String
    get() {
      val format = DecimalFormat("#,##0.00")
      return format.format(jurosMensal)
    }
  val locaisPagamento
    get() = arrayOf("EM QUALQUER BANCO OU CORRESP. BANCARIO MESMO APOS O VENCIMENTO")
  
  fun buildBeneficiario(nossoNumero: Int) = Beneficiario.novoBeneficiario()
    .comNomeBeneficiario(dadosBeneficiario.nome)
    .comAgencia(agencia)
    .comDigitoAgencia(digitoAgencia)
    .comCodigoBeneficiario(codigo)
    .comDigitoCodigoBeneficiario(digitoCodigo)
    .comNumeroConvenio(numeroConvenio)
    .comCarteira(carteira)
    .comEndereco(endereco)
    .comNossoNumero("$nossoNumero")
    .comDigitoNossoNumero(banco.geradorDeDigito
                            .geraDigitoMod10("$agencia$codigo$carteira$nossoNumero")
                            .toString())
    .comDocumento(dadosBeneficiario.documento)
  
  companion object {
    private val dadosBeneficiario = DadosBeneficiario.DADOS_BENEFICIARIO!!
    private val BANCO_ITAU = Itau()
    val CONVENIO_ITAU = DadosConvenio(agencia = "0344",
                                      digitoAgencia = "",
                                      codigo = "00278",
                                      digitoCodigo = "7",
                                      numeroConvenio = "0",
                                      carteira = "109",
                                      endereco = dadosBeneficiario.buidendereco(),
                                      digitoNossoNumero = "",
                                      banco = BANCO_ITAU,
                                      dadosBeneficiario = dadosBeneficiario,
                                      jurosMensal = 7.9)
  }
}