package br.com.astrosoft.boletoSaci.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB

class QuerySaci: QueryDB(driver, url, username, password) {
  fun dadosPagador(loja: Int, contrato: Int): List<DadosBoleto> {
    val sql = "/sql/dadosPagador.sql"
    return query(sql) {q ->
      q.addParameter("loja", loja)
      q.addParameter("contrato", contrato)
      q.executeAndFetch(DadosBoleto::class.java)
    }
  }
  
  fun dadosBoletos(lote: Int): List<DadosBoleto> {
    val sql = "/sql/dadosBoletos.sql"
    return query(sql) {q ->
      q.addParameter("lote", lote)
      q.executeAndFetch(DadosBoleto::class.java)
    }
  }
  
  fun lotes(): List<Lote> {
    val sql = "/sql/lotes.sql"
    return query(sql) {q ->
      q.executeAndFetch(Lote::class.java)
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
  
  fun updateParcela(loja: Int, contrato: Int, parcela: Int, nossoNumero: Int, processado : Boolean) {
    val sql = "/sql/updateParcela.sql"
    script(sql) {q ->
      q.addParameter("loja", loja)
      q.addParameter("contrato", contrato)
      q.addParameter("parcela", parcela)
      q.addParameter("nossoNumero", nossoNumero)
      q.addParameter("processado", processado)
      q.executeUpdate()
    }
  }
  
  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

val saci = QuerySaci()