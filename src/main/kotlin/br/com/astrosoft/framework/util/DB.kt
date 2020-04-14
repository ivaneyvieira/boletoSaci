package br.com.astrosoft.framework.util

class DB(banco: String): ConfigFile() {
  val driver = getValue("datasource.$banco.databaseDriver") ?: ""
  val url = getValue("datasource.$banco.databaseUrl") ?: ""
  val username = getValue("datasource.$banco.username") ?: ""
  val password = getValue("datasource.$banco.password") ?: ""
}