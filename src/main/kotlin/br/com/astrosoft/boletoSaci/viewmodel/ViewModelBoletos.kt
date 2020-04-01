package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail

class ViewModelBoletos(view: IViewModelBoletos): ViewModel<IViewModelBoletos>(view) {
  fun adicionarBoleto() {
    view.openAdicionaParcelas()
  }
  
  fun gerarRemessa() = exec {
    val dadosBoleto = boletosGerados().filter {dados ->
      !dados.processado
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
    return saci.dadosBoletos()
      .sortedWith(compareBy(DadosBoleto::storeno, DadosBoleto::contrno,
                            DadosBoleto::instno))
  }
}

interface IViewModelBoletos: IView {
  fun updateGrid(list: List<DadosBoleto>)
  
  fun openAdicionaParcelas()
  
  fun openText(dadosBoleto: List<DadosBoleto>)
}