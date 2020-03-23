package br.com.astrosoft.boletoSaci.viewmodel

import br.com.astrosoft.boletoSaci.model.BoletoSaci
import br.com.astrosoft.boletoSaci.model.DadosConvenio
import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.boletoSaci.model.saci
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail

class ViewModelRelatorio(view: IViewRelatorio): ViewModel<IViewRelatorio>(view) {
  fun geraBoleto() = exec{
    val loja = view.loja ?: fail("Loja inválida")
    val contrato = view.contrato ?: fail("Contrato Inválido")
    val dadosPagador = saci.dadosPagador(loja, contrato)
    val contratos = DadosPagador.contratosPagador(dadosPagador)
    val dadosConvenio = DadosConvenio.CONVENIO_ITAU
    val bytesBoletos = BoletoSaci(contratos, dadosConvenio).geraBoleto()
    view.updateBoleto(bytesBoletos)
  }
}

interface IViewRelatorio: IView {
  val loja: Int?
  val contrato: Int?
  val parcela: Int?
  
  fun updateBoleto(bytesBoletos : ByteArray)
}