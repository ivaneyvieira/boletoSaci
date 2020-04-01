package br.com.astrosoft.boletoSaci.model.arquivosBancario

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.util.rpad
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.reflect.KProperty1

abstract class Field<B, T>(val pos: Int, val len: Int, val property: KProperty1<B, T?>) {
  fun line(bean: B): String {
    val value = property.get(bean)
    return toStr(value)
  }
  
  fun readLine(linha: String): T? {
    return toValue(linha.mid(pos - 1, len))
  }
  
  abstract fun toValue(str: String?): T?
  abstract fun toStr(value: T?): String
}

class FieldAlfanumerico<B>(pos: Int, len: Int, property: KProperty1<B, String>): Field<B, String>(pos, len, property) {
  override fun toValue(str: String?): String? {
    return str?.trim() ?: ""
  }
  
  override fun toStr(value: String?): String {
    value ?: return "".rpad(len, "0")
    return value.rpad(len, " ")
  }
}

class FieldNumber<B>(pos: Int, len: Int, property: KProperty1<B, Long?>): Field<B, Long>(pos, len, property) {
  override fun toValue(str: String?): Long? {
    return str?.toLong()
  }
  
  override fun toStr(value: Long?): String {
    value ?: return "".lpad(len, "0")
    return value.toString()
      .lpad(len, "0")
  }
}

class FieldDouble<B>(pos: Int, len: Int, prec : Int, property: KProperty1<B, Double?>): Field<B, Double>(pos, len,
                                                                                                    property) {
  private val fator = 10.00.pow(prec * 1.00)
  override fun toValue(str: String?): Double? {
    return str?.toLong()
      ?.div(fator)
  }
  
  override fun toStr(value: Double?): String {
    value ?: return "".lpad(len, "0")
    return value.times(fator)
      .roundToLong()
      .toString()
      .lpad(len, "0")
  }
}

class FieldDate<B>(pos: Int, property: KProperty1<B, LocalDate?>): Field<B, LocalDate?>(pos, 6, property) {
  private val formatDate: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy")
  override fun toValue(str: String?): LocalDate? {
    return try {
      LocalDate.parse(str, formatDate)
    } catch(e: DateTimeParseException) {
      null
    }
  }
  
  override fun toStr(value: LocalDate?): String {
    value ?: return "0".lpad(len, "0")
    return value.format(formatDate)
  }
}
