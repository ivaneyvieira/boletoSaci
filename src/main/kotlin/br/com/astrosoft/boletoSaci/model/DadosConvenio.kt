package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.framework.util.lpad
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
  val instrucoes
    get() = arrayOf("APÓS O VENCIMENTO COBRAR JUROS DE $jurosFormatado AO MES",
                    "APÓS 30 DIAS DO VENCIMENTO CONTATE NOSSO",
                    "SETR D COBRANÇA TELEFONE/WHATSAPP 86 2107-4000")
  private val jurosFormatado: String
    get() {
      val format = DecimalFormat("#,##0.00")
      return format.format(jurosMensal)
    }
  val locaisPagamento
    get() = arrayOf("", "")
  
  fun buildBeneficiario(nossoNumero : Int) = Beneficiario.novoBeneficiario()
    .comNomeBeneficiario(dadosBeneficiario.nome)
    .comAgencia(agencia)
    .comDigitoAgencia(digitoAgencia)
    .comCodigoBeneficiario(codigo)
    .comDigitoCodigoBeneficiario(digitoCodigo)
    .comNumeroConvenio(numeroConvenio)
    .comCarteira(carteira)
    .comEndereco(endereco)
    .comNossoNumero("$nossoNumero".lpad(8,"0"))
    .comDigitoNossoNumero(digitoNossoNumero)
    .comDocumento(dadosBeneficiario.documento)
  
  companion object {
    private val dadosBeneficiario = DadosBeneficiario.DADOS_BENEFICIARIO!!
    val CONVENIO_ITAU = DadosConvenio(agencia = "0344",
                                      digitoAgencia = "",
                                      codigo = "00278",
                                      digitoCodigo = "7",
                                      numeroConvenio = "0",
                                      carteira = "157",
                                      endereco = dadosBeneficiario.buidendereco(),
                                      digitoNossoNumero = "1",
                                      banco = Itau(),
                                      dadosBeneficiario = dadosBeneficiario,
                                      jurosMensal = 7.90)
  }
}