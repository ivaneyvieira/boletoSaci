package br.com.astrosoft.boletoSaci.model.arquivosBancario

import java.time.LocalDate
import kotlin.reflect.KProperty1

class Registro<B> {
  private val fields = mutableListOf<Field<B, *>>()
  
  fun registro(block: Registro<B>.() -> Unit) {
    block()
  }
  
  private fun pos(): Int = fields.sumBy {it.len} + 1
  
  fun alpha(len: Int, property: KProperty1<B, String>) {
    fields += FieldAlfanumerico(pos(), len, property)
  }
  
  fun number(len: Int, property: KProperty1<B, Long>) {
    fields += FieldNumber(pos(), len, property)
  }
  
  fun money(len: Int, property: KProperty1<B, Double>) {
    fields += FieldDouble(pos(), len, 2, property)
  }
  
  fun quant(len: Int, property: KProperty1<B, Double>) {
    fields += FieldDouble(pos(), len, 4, property)
  }
  
  fun date(property: KProperty1<B, LocalDate?>) {
    fields += FieldDate(pos(), property)
  }
  
  fun line(bean: B): String {
    return fields.joinToString(separator = "") {field ->
      field.line(bean)
    }
  }
}

