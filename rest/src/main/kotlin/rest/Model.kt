package rest

import java.time.LocalDate

data class Contrato(
  val numLojaVenda: Int,
  val numContrato: Int,
  val cliente : Cliente,
  val parcelas : List<Parcela>
                   )

data class Cliente(
  val codigo: Int,
  val nome: String,
  val documento: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val email: String,
  val uf: String
                  )

data class Parcela(
  val numParcela: Int,
  val statusParcela: String,
  val valorParcela: Double,
  val valorJuros: Double,
  val dtVencimento: LocalDate
                  )