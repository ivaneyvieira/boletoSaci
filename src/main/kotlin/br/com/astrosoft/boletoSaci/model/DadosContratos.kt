package br.com.astrosoft.boletoSaci.model

import java.util.*

class DadosContratos(
  val storeno: Int,
  val contrno: Int,
  val status: Int,
  val codigo: Int,
  val documento: String,
  val nome: String,
  val data: Date,
  val valor: Double
                    ) {
  val parcelas
    get() = saci.dadosPagador(storeno, contrno)
}