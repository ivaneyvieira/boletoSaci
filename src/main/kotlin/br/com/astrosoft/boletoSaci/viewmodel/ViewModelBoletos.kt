package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.util.toDate
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import java.time.LocalDate
import java.util.*

class ViewModelBoletos(view: IViewModelBoletos): ViewModel<IViewModelBoletos>(view) {
  fun adicionarBoleto() {
    view.openAdicionaParcelas()
  }
  
  fun gerarRemessa() = exec {
    val dadosBoleto = boletosGerados().filter {dados ->
      true
    }
    dadosBoleto.ifEmpty {
      fail("Não há nenhum boleto para processar.")
    }
    view.openText(dadosBoleto)
    updateGrid()
  }
  
  fun updateGrid() = exec {
    view.updateGrid(boletosGerados())
  }
  
  fun boletosGerados(): List<DadosBoleto> {
    val boletos = saci.dadosBoletos()
    val boletosAgrupados = boletos.groupBy {
      AgrupamentoBoleto(it.codigo, it.nossoNumero)
    }
      .map {(agrupamento, boletos) ->
        DadosBoleto(
          storeno = 0,
          contrno = boletos.map {it.contrno}
                      .max() ?: 0,
          instno = 0,
          statusParcela = 0,
          statusContrato = 0,
          nossoNumero = agrupamento.nossoNumero,
          valorParcela = boletos.sumByDouble {it.valorParcela},
          valorJuros = boletos.sumByDouble {it.valorJuros},
          dtVencimento = dataVencimento(boletos.mapNotNull {it.dtVencimento}
                                          .maxBy {it}),
          codigo = agrupamento.codigo,
          nome = boletos.firstOrNull()?.nome ?: "",
          documento = boletos.firstOrNull()?.documento ?: "",
          endereco = boletos.firstOrNull()?.endereco ?: "",
          bairro = boletos.firstOrNull()?.bairro ?: "",
          cep = boletos.firstOrNull()?.cep ?: "",
          cidade = boletos.firstOrNull()?.cidade ?: "",
          uf = boletos.firstOrNull()?.uf ?: "",
          dtProcessamento = boletos.firstOrNull()?.dtProcessamento
                   )
      }
  
    return boletosAgrupados.sortedWith(compareBy(DadosBoleto::storeno, DadosBoleto::contrno,
                                                 DadosBoleto::instno))
  }
  
  fun dataVencimento(data: Date?): Date? {
    val dataLimite =
      LocalDate.of(2020, 4, 10)
        .toDate()!!
    data ?: return dataLimite
    return if(data.before(dataLimite))
      dataLimite
    else data
  }
}

interface IViewModelBoletos: IView {
  fun updateGrid(list: List<DadosBoleto>)
  
  fun openAdicionaParcelas()
  
  fun openText(dadosBoleto: List<DadosBoleto>)
}

data class AgrupamentoBoleto(val codigo: Int, val nossoNumero: Int)