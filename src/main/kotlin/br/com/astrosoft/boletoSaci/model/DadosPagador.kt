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
  val nossoNumero: Int,
  val valorParcela: Double,
  val valorJuros: Double,
  val dtVencimento: Date,
  val nome: String,
  val documento: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val uf: String
                       ) {
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
    get() = dtVencimento?.toLocalDate()
  
  fun buildContrato() = Contrato(storeno, contrno, statusContrato, nome, nossoNumero, documento,
                                 endereco, bairro, cep,
                                 cidade, uf)
  
  fun buildPrestacoes() =
    Prestacao(storeno, contrno, instno, statusParcela, valorParcela, valorJuros, dtVencimento.toLocalDate())
  
  companion object {
    fun contratosPagador(pagamentos: List<DadosPagador>): List<Contrato> {
      val contratos = pagamentos.groupBy {dados ->
        dados.buildContrato()
      }
      return contratos.map {(contrato, prestacoes) ->
        contrato.apply {
          this.prestacoes = prestacoes.map {dados ->
            dados.buildPrestacoes()
          }
        }
      }
    }
  }
}

data class Contrato(
  val storeno: Int,
  val contrno: Int,
  val statusContrato: Int,
  val nome: String,
  val nossoNumero: Int,
  val documento: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val uf: String
                   ) {
  var prestacoes: List<Prestacao> = emptyList()
  val dtVencimento
    get() = prestacoes.map {it.dtVencimento}
      .filterNotNull()
      .min()
  val valorParcela
    get() = prestacoes.map {it.valorParcela}
      .sum()
  val numeroDocumento
    get() = "$contrno"
  
  fun buildDatas() = Datas.novasDatas()
    .comDocumento(LocalDate.now()
                    .toCalendar())
    .comProcessamento(LocalDate.now()
                        .toCalendar())
    .comVencimento(dtVencimento?.toCalendar())
  
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
  
  private fun LocalDate.toCalendar(): Calendar? {
    val calendar = Calendar.getInstance()
    calendar.clear()
    
    calendar.set(this.year, this.monthValue - 1, this.dayOfMonth)
    return calendar
  }
}

data class Prestacao(
  val storeno: Int,
  val contrno: Int,
  val instno: Int,
  val statusParcela: Int,
  val valorParcela: Double,
  val valorJuros: Double,
  val dtVencimento: LocalDate?
                    )