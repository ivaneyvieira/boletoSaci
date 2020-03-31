package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.boletoSaci.model.arquivosBancario.ArquivoRemessaItau
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ViewModelBoletos(view: IViewModelBoletos): ViewModel<IViewModelBoletos>(view) {
  fun adicionarBoleto() {
    view.openAdicionaParcelas()
  }
  
  fun gerarRemessa() = exec {
    val dadosPagador = boletosGerados()
    val dadosConvenio = DadosConvenio.CONVENIO_ITAU
    val boletosSaci = BoletoSaci(dadosPagador, dadosConvenio)
    val boletos = boletosSaci.buildListBoleto()
    val arquivoRemessa = ArquivoRemessaItau()
    val arquivo = arquivoRemessa.buildFile(boletos)
    val arquivoStr = arquivo.joinToString(separator = "\r\n")
    view.openText(arquivoStr)
    updateGrid()
  }
  
  fun updateGrid() = exec {
    view.updateGrid(boletosGerados())
  }
  
  fun boletosGerados(): List<DadosPagador> {
    return saci.dadosBoletos().sortedWith(compareBy(DadosPagador::storeno, DadosPagador::contrno,
                                                    DadosPagador::instno))
  }
}

interface IViewModelBoletos: IView {
  fun updateGrid(list: List<DadosPagador>)
  
  fun openAdicionaParcelas()
  
  fun openText(arquivoStr: String)
}