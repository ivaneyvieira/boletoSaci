package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.boletoSaci.viewmodel.IViewPesquisaParcelas
import br.com.astrosoft.boletoSaci.viewmodel.ViewModelPesquisaParcelas
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.addColumnDate
import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.caelum.stella.format.CNPJFormatter
import br.com.caelum.stella.format.CPFFormatter
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route

@Route(value = "pesquisa", layout = MainAppLayout::class)
@Caption("Pesquisa Prestações")
@Icon(VaadinIcon.FORM)
@HtmlImport("frontend://styles/shared-styles.html")
class ViewPesquisaParcelas: IViewPesquisaParcelas, ViewLayout<ViewModelPesquisaParcelas>() {
  private val cpfFormater = CPFFormatter()
  private val cnpjFormater = CNPJFormatter()
  private lateinit var edtDoc: TextField
  private lateinit var edtCodigo: TextField
  private val dataProviderParcelas = ListDataProvider<DadosPagador>(mutableListOf())
  override val viewModel = ViewModelPesquisaParcelas(this)
  
  init {
    horizontalLayout {
      edtCodigo = textField("Código do Cliente") {
        value = "123456"
      }
      edtDoc = textField("CPF/CNPJ") {
      }
      
      button("Pesquisa") {
        addThemeVariants(LUMO_PRIMARY)
        onLeftClick {
          viewModel.pesquisaCliente()
        }
      }
    }
    grid(dataProviderParcelas) {
      isExpand = true
      isMultiSort = true
      addThemeVariants(LUMO_COMPACT)
      setSelectionMode(SelectionMode.MULTI)
      
      addColumnInt(DadosPagador::storeno) {
        setHeader("Loja")
      }
      addColumnInt(DadosPagador::contrno) {
        setHeader("Contrato")
      }
      
      
      addColumnInt(DadosPagador::instno) {
        setHeader("Parcela")
      }
      addColumnDate(DadosPagador::localDtVencimento) {
        setHeader("Vencimento")
      }
      addColumnDouble(DadosPagador::valorParcela) {
        setHeader("Valor")
      }
      addColumnString(DadosPagador::descricaoStatus) {
        setHeader("Situação")
      }
      
      this.setClassNameGenerator {
        when {
          it.statusParcela != 0 -> "liquidada"
          else                  -> null
        }
      }
    }
  }
  
  override val documento: String
    get() {
      val strDoc = edtDoc.value ?: return ""
      return when {
        cnpjFormater.canBeFormatted(strDoc) -> cnpjFormater.format(strDoc)
        cpfFormater.canBeFormatted(strDoc)  -> cpfFormater.format(strDoc)
        else                                -> ""
      }
    }
  override val codigoCliente: Int
    get() = edtCodigo.value?.toIntOrNull() ?: 0
  
  override fun updateCliente(parcelas: List<DadosPagador>) {
    val lista = parcelas.sortedWith(compareBy(DadosPagador::storeno, DadosPagador::contrno,
                                              DadosPagador::instno))
      .distinct()
    dataProviderParcelas.items.clear()
    dataProviderParcelas.items.addAll(lista)
    dataProviderParcelas.refreshAll()
  }
}