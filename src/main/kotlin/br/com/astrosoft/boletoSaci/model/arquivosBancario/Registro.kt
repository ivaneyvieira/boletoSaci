package br.com.astrosoft.boletoSaci.model.arquivosBancario

import java.time.LocalDate
import kotlin.reflect.KProperty1

class Registro<B> {
  private val fields = mutableListOf<Field<B, *>>()
  
  fun registro(block: Registro<B>.() -> Unit) {
    block()
  }
  
  private fun nextPos(): Int = fields.sumBy {it.len} + 1
  
  fun alpha(pos: Int, len: Int, property: KProperty1<B, String>) {
    validaPos(pos, property)
    fields += FieldAlfanumerico(pos, len, property)
  }
  
  private fun validaPos(pos: Int, property: KProperty1<*, *>) {
    if(pos != nextPos())
      print("######### Erro ${property.name} posicao $pos <> ${nextPos()}")
  }
  
  fun number(pos: Int, len: Int, property: KProperty1<B, Long>) {
    validaPos(pos, property)
    fields += FieldNumber(pos, len, property)
  }
  
  fun money(pos: Int, len: Int, property: KProperty1<B, Double>) {
    validaPos(pos, property)
    fields += FieldDouble(pos, len, 2, property)
  }
  
  fun quant(pos: Int, len: Int, property: KProperty1<B, Double>) {
    validaPos(pos, property)
    fields += FieldDouble(pos, len, 4, property)
  }
  
  fun date(pos: Int, property: KProperty1<B, LocalDate?>) {
    validaPos(pos, property)
    fields += FieldDate(pos, property)
  }
  
  fun line(bean: B): String {
    return fields.joinToString(separator = "") {field ->
      val value = field.line(bean)
      val len = field.len
      if(len != value.length)
        print("#### Erro ${field.property.name} $len <> ${value.length} $value")
      value
    }
  }
}

