package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.framework.util.toLocalDate
import br.com.caelum.stella.boleto.Datas
import br.com.caelum.stella.boleto.Endereco
import br.com.caelum.stella.boleto.Pagador
import java.time.LocalDate
import java.util.*

data class DadosPagador(
  val storeno: Int,
  val contrno: Int,
  val instno: Int,
  val statusParcela: Int,
  val statusContrato: Int,
  var nossoNumero: Int,
  val valorParcela: Double,
  val valorJuros: Double,
  val dtVencimento: Date,
  val codigo: Int,
  val nome: String,
  val documento: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val uf: String,
  val processado: Boolean
                       ) {
  val chaveERP
    get() = "$storeno-$contrno-$instno"
  val boletoEmitido
    get() = nossoNumero > 0
  val descricaoStatus
    get() = when(statusParcela) {
      0    -> "Aberto"
      1    -> "Liquidado"
      2    -> "Parcial"
      3    -> "SPC"
      4    -> "Cobrador"
      5    -> "Cancelado"
      else -> ""
    }
  val localDtVencimento
    get() = dtVencimento.toLocalDate()
  val enderecoPagador: Endereco
    get() = Endereco.novoEndereco()
      .comLogradouro(endereco)
      .comBairro(bairro)
      .comCep(cep.formataCep())
      .comCidade(cidade)
      .comUf(uf)
  
  fun buildPagador() = Pagador.novoPagador()
    .comNome(nome)
    .comDocumento(documento)
    .comEndereco(enderecoPagador)
  
  private fun LocalDate?.toCalendar(): Calendar? {
    this ?: return null
    val calendar = Calendar.getInstance()
    calendar.clear()
    
    calendar.set(this.year, this.monthValue - 1, this.dayOfMonth)
    return calendar
  }
  
  val numeroDocumento
    get() = "$contrno"
  
  fun buildDatas() = Datas.novasDatas()
    .comDocumento(LocalDate.now()
                    .toCalendar())
    .comProcessamento(LocalDate.now()
                        .toCalendar())
    .comVencimento(localDtVencimento.toCalendar())
  
  val isExpired
    get() = localDtVencimento?.isBefore(LocalDate.now()) ?: true
  
  fun updateNossoNumero(): Int {
    val novoNossoNumero = saci.proximoNumero()
    saci.updateParcela(loja = storeno,
                       contrato = contrno,
                       parcela = instno,
                       nossoNumero = novoNossoNumero,
                       processado = false)
    nossoNumero = novoNossoNumero
    return novoNossoNumero
  }
  
  fun updateProcessamento(): Int {
    if(boletoEmitido)
      saci.updateParcela(loja = storeno,
                         contrato = contrno,
                         parcela = instno,
                         nossoNumero = nossoNumero,
                         processado = true)
    return nossoNumero
  }
}
