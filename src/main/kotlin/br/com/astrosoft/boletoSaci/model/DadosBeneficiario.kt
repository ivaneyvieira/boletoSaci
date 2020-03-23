package br.com.astrosoft.boletoSaci.model

import br.com.caelum.stella.boleto.Endereco

class DadosBeneficiario(
  val nome: String,
  val documento: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val uf: String
                       ) {
  fun buidendereco() = Endereco.novoEndereco()
    .comLogradouro(endereco)
    .comBairro(bairro)
    .comCep(cep.formataCep())
    .comCidade(cidade)
    .comUf(uf)
  
  companion object {
    val DADOS_BENEFICIARIO: DadosBeneficiario? = saci.dadosBeneficiario()
  }
}