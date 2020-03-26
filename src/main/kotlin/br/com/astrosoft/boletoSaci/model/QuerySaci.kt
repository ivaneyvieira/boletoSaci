package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB

class QuerySaci: QueryDB(driver, url, username, password) {
  fun dadosPagador(loja: Int, contrato: Int): List<DadosPagador> {
    val sql = "/sql/dadosPagador.sql"
    return query(sql) {q ->
      q.addParameter("loja", loja)
      q.addParameter("contrato", contrato)
      q.executeAndFetch(DadosPagador::class.java)
    }
  }
  
  fun dadosBeneficiario(): DadosBeneficiario? {
    val sql = "/sql/dadosBeneficiario.sql"
    return query(sql) {q ->
      q.executeAndFetch(DadosBeneficiario::class.java)
        .firstOrNull()
    }
  }
  
  fun proximoNumero(): Int {
    val sql = "/sql/proximoNumero.sql"
    return query(sql) {q ->
      val numero = q.executeScalarList(Int::class.java)
        .firstOrNull() ?: 10000000
      if(numero == 0) 10000000
      else numero
    } + 1
  }
  
  fun localizaContratos(codigo: Int, documento : String): List<DadosContratos> {
    val sql = "/sql/localizaContratos.sql"
    return query(sql) {q ->
      q.addParameter("codigo", codigo)
      q.addParameter("documento", documento)
      q.executeAndFetch(DadosContratos::class.java)
    }
  }
  
  fun updateBoleto(loja: Int, contrato: Int, parcela: Int, nossoNumero: Int) {
    val sql = "/sql/novoBoleto.sql"
    script(sql) {q ->
      q.addParameter("loja", loja)
      q.addParameter("contrato", contrato)
      q.addParameter("parcela", parcela)
      q.addParameter("nossoNumero", nossoNumero)
      q.executeUpdate()
    }
  }
  
  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

val saci = QuerySaci()