package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.framework.util.toDate
import java.time.LocalDate
import java.util.*

class BoletoFuncionario(
  val documento: String,
  val nossoNumero: Int,
  val numLote: Int,
  val valor: Double,
  val codigo: Int,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cep: String,
  val cidade: String,
  val uf: String
                       ) {
  fun dadosBoleto(dataVencimento : LocalDate)= DadosBoleto(
      storeno = 1,
      contrno = nossoNumero,
      instno = numLote,
      statusParcela = 0,
      statusContrato = 0,
      nossoNumero = nossoNumero,
      valorParcela = valor,
      valorJuros = 0.00,
      dtVencimento = dataVencimento.toDate(),
      dtEmissao = LocalDate.now()
        .toDate(),
      codigo = codigo,
      nome = nome,
      documento = documento,
      endereco = endereco,
      bairro = bairro,
      cep = cep,
      cidade = cidade,
      uf = uf,
      email = "",
      dtProcessamento = LocalDate.now()
        .toDate(),
      dtVencimentoBoleto = LocalDate.now()
        .plusDays(7)
        .toDate(),
      numLote = numLote
                       )
}