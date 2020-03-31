package br.com.astrosoft.boletoSaci.model.arquivosBancario

import br.com.caelum.stella.boleto.Boleto

open class Arquivo<H, D, T> {
  val header = Registro<H>()
  val detail = Registro<D>()
  val trailer = Registro<T>()
  
  protected fun header(block: Registro<H>.() -> Unit): Registro<H> {
    header.block()
    return header
  }
  
  protected fun detail(block: Registro<D>.() -> Unit): Registro<D> {
    detail.block()
    return detail
  }
  
  protected fun trailer(block: Registro<T>.() -> Unit): Registro<T> {
    trailer.block()
    return trailer
  }
}




