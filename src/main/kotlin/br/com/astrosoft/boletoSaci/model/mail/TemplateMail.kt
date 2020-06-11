package br.com.astrosoft.boletoSaci.model.mail

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.framework.util.format

object TemplateMail {
  private val arquivo = SystemUtils.readFile("/html/templateEmail.html") ?: ""
  
  private val arquivoSite = SystemUtils.readFile("/html/templateEmailSite.html") ?: ""
  
  fun corpoEmailHTML(codigo: Int, boletosCliente: List<DadosBoleto>): String {
    val contratos = boletosCliente.groupBy {!it.boletoVencido}
    val tableHtmlContratos = tableHtmlContrato(contratos)
    return arquivo.replace("<%PARCELAS%>", tableHtmlContratos)
  }
  
  fun corpoEmailSiteHTML(): String {
    return arquivoSite
  }
  private fun tableHtmlContrato(contratos: Map<Boolean, List<DadosBoleto>>): String {
    return contratos.entries.joinToString(separator = "\n") {dados ->
      dados.value.sortedBy {it.dtVencimento}
        .joinToString("\n") {dados ->
          """
                    <tr>
                        <td style="text-align: center;">${dados.dtVencimento.format("MM/yy")}</td>
                        <td style="text-align: center;">${dados.storeno}</td>
                        <td style="text-align: center;">${dados.contrno}</td>
                        <td style="text-align: center;">${dados.dtEmissao.format()}</td>
                        <td style="text-align: center;">${dados.dtVencimento.format()}</td>
                        <td style="text-align: right;">${dados.valorParcela.format()}</td>
                        <td style="text-align: right;">${dados.valorTotal.format()}</td>
                    </tr>
      """.trimIndent()
        } + "\n" +
      linhaTotal(dados.value, "Total Parcial", dados.value.size > 1)
    } + "\n" +
           linhaTotal(contratos.values.flatten(), "Total Geral", contratos.values.flatten().size > 1)
  }
  
  private fun linhaTotal(contratos: List<DadosBoleto>, strTotal: String, print: Boolean): String {
    return if(print)
      """
               <tr>
                   <td colspan=5>$strTotal</td>
                   <td style="text-align: right;">${contratos.sumByDouble {it.valorParcela}
        .format()}</td>
                   <td style="text-align: right;">${contratos.sumByDouble {it.valorTotal}
        .format()}</td>
               </tr>
         """.trimIndent()
    else ""
  }
}