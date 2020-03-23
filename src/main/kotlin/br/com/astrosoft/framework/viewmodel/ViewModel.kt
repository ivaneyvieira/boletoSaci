package br.com.astrosoft.framework.viewmodel


open class ViewModel<V: IView>(val view: V) {
  fun exec(block: () -> Unit) {
    try {
      block()
    } catch(e: EViewModelFail) {
      view.showError(e.message ?: "Erro generico")
      throw e
    }
  }
  
  open fun init() {
  }
}

fun fail(message: String): Nothing {
  throw EViewModelFail(message)
}

interface IView {
  fun showError(msg: String)
  fun showWarning(msg: String)
  fun showInformation(msg: String)
}