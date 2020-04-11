package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.Lote
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail

class ViewModelBoletos(view: IViewModelBoletos): ViewModel<IViewModelBoletos>(view) {
  fun adicionarBoleto() {
    view.openAdicionaParcelas()
  }
  
  fun gerarRemessa() = exec {
    val dadosBoleto = boletosGerados()
    dadosBoleto.ifEmpty {
      fail("Não há nenhum boleto para processar.")
    }
    view.openText(dadosBoleto)
    updateGrid()
  }
  
  fun updateGrid() = exec {
    view.updateGrid(boletosGerados())
  }
  
  fun lotes(): List<Lote> {
    return saci.lotes()
  }
  
  val dadosBoleto: List<DadosBoleto>
    get() {
      val lote = view.lote ?: return emptyList()
      return saci.dadosBoletos(lote.numLote)
    }
  
  fun boletosGerados(): List<DadosBoleto> {
    val boletos = dadosBoleto
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
          dtVencimento = boletos.mapNotNull {it.dtVencimentoBoleto}
            .maxBy {it},
          dtEmissao = boletos.mapNotNull {it.dtEmissao}
            .minBy {it},
          codigo = agrupamento.codigo,
          nome = boletos.firstOrNull()?.nome ?: "",
          documento = boletos.firstOrNull()?.documento ?: "",
          endereco = boletos.firstOrNull()?.endereco ?: "",
          bairro = boletos.firstOrNull()?.bairro ?: "",
          cep = boletos.firstOrNull()?.cep ?: "",
          cidade = boletos.firstOrNull()?.cidade ?: "",
          uf = boletos.firstOrNull()?.uf ?: "",
          email = boletos.firstOrNull()?.email ?: "",
          dtProcessamento = boletos.firstOrNull()?.dtProcessamento,
          dtVencimentoBoleto = boletos.mapNotNull {it.dtVencimentoBoleto}
            .maxBy {it},
          numLote = boletos.firstOrNull()?.numLote ?: 0
                   )
      }
    
    return boletosAgrupados.sortedWith(compareBy(DadosBoleto::storeno, DadosBoleto::contrno,
                                                 DadosBoleto::instno))
  }
}

interface IViewModelBoletos: IView {
  fun updateGrid(list: List<DadosBoleto>)
  
  fun openAdicionaParcelas()
  
  fun openText(dadosBoleto: List<DadosBoleto>)
  
  val lote: Lote?
}

data class AgrupamentoBoleto(val codigo: Int, val nossoNumero: Int)