package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ViewModelPesquisaParcelas(view: IViewPesquisaParcelas): ViewModel<IViewPesquisaParcelas>(view) {
  fun pesquisaCliente() {
    val codigoCliente = view.codigoCliente
    val documento = view.documento
    val contratos = saci.localizaContratos(codigoCliente, documento)
    val parcelas = contratos.flatMap {contrato ->
      contrato.parcelas
    }
    view.updateCliente(parcelas)
    parcelas.ifEmpty {
      view.showError("Não foram encontrados parcelas")
    }
  }
  
  fun processaParcelas() {
    val parcelas = view.parcelasSelecionadas

    view.imprimeBoletos(parcelas)
    pesquisaCliente()
  }
}

interface IViewPesquisaParcelas: IView {
  val documento: String
  val codigoCliente: Int
  val parcelasSelecionadas: List<DadosBoleto>
  fun updateCliente(parcelas: List<DadosBoleto>)
  fun imprimeBoletos(dadosBoletos: List<DadosBoleto>)
}