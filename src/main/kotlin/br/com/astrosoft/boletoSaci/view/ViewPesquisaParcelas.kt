package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.Contrato
import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.boletoSaci.viewmodel.IViewPesquisaParcelas
import br.com.astrosoft.boletoSaci.viewmodel.ViewModelPesquisaParcelas
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.addColumnBool
import br.com.astrosoft.framework.view.addColumnDate
import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.caelum.stella.format.CNPJFormatter
import br.com.caelum.stella.format.CPFFormatter
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.flexGrow
import com.github.mvysny.karibudsl.v10.flexShrink
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.responsiveSteps
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v10.tooltip
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_ALIGN_RIGHT
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import java.text.DecimalFormat

@Route(value = "pesquisa", layout = MainAppLayout::class)
@Caption("Pesquisa Prestações")
@Icon(VaadinIcon.FORM)
@CssImport("frontend://styles/grid.css", themeFor = "vaadin-grid")
class ViewPesquisaParcelas: IViewPesquisaParcelas, ViewLayout<ViewModelPesquisaParcelas>() {
  private var gridParcelas: Grid<DadosPagador>
  private lateinit var lblValorTotal: TextField
  private lateinit var lblNomeCleinte: TextField
  private val cpfFormater = CPFFormatter()
  private val cnpjFormater = CNPJFormatter()
  private lateinit var edtDoc: TextField
  private lateinit var edtCodigo: TextField
  private val dataProviderParcelas = ListDataProvider<DadosPagador>(mutableListOf())
  override val viewModel = ViewModelPesquisaParcelas(this)
  
  init {
    horizontalLayout {
      setWidthFull()
      edtCodigo = textField("Código") {
        width = "100px"
        value = "123456"
        tooltip = "Código do cliente"
      }
      edtDoc = textField("CPF/CNPJ") {
        width = "300px"
      }
      lblNomeCleinte = textField("Cliente") {
        width = "100%"
        isReadOnly = true
      }
      lblValorTotal = textField("Total") {
        width = "100px"
        addThemeVariants(LUMO_ALIGN_RIGHT)
        isReadOnly = true
      }
      
      horizontalLayout {
        isExpand = true
        setWidthFull()
        justifyContentMode = JustifyContentMode.END
        button("Pesquisa") {
          icon = VaadinIcon.SEARCH.create()
          addThemeVariants(LUMO_PRIMARY)
          onLeftClick {
            viewModel.pesquisaCliente()
          }
        }
        button("Gera Boleto") {
          icon = VaadinIcon.BARCODE.create()
          addThemeVariants(LUMO_SUCCESS)
          onLeftClick {
            viewModel.processaParcelas()
          }
        }
      }
    }
    gridParcelas = grid(dataProviderParcelas) {
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
      addColumnBool(DadosPagador::boletoEmitido){
        setHeader("Boleto Emitido")
      }
      this.setClassNameGenerator {
        when {
          !it.podeSelecionar() -> "liquidada"
          else                 -> null
        }
      }
      setValorTotal(0.00)
      
      this.addSelectionListener {event ->
        val naoSelecionado = event.allSelectedItems.filter {!it.podeSelecionar()}
        val valorTotal =
          event.allSelectedItems.filter {it.podeSelecionar()}
            .sumByDouble {it.valorParcela}
        setValorTotal(valorTotal)
        naoSelecionado.forEach {
          selectionModel.deselect(it)
        }
      }
    }
  }
  
  override val documento: String
    get() {
      val strDoc =
        edtDoc.value?.replace(".", "")
          ?.replace("-", "")
          ?.replace("/", "") ?: return ""
      return when {
        cnpjFormater.canBeFormatted(strDoc) -> cnpjFormater.format(strDoc)
        cpfFormater.canBeFormatted(strDoc)  -> cpfFormater.format(strDoc)
        else                                -> ""
      }
    }
  override val codigoCliente: Int
    get() = edtCodigo.value?.toIntOrNull() ?: 0
  override val parcelasSelecionadas: List<DadosPagador>
    get() = gridParcelas.selectedItems.filter {it.podeSelecionar()}
      .toList()
  
  override fun updateCliente(parcelas: List<DadosPagador>) {
    val lista = parcelas.sortedWith(compareBy(DadosPagador::storeno, DadosPagador::contrno,
                                              DadosPagador::instno))
      .distinct()
    val parcela = parcelas.firstOrNull()
    edtDoc.value = documento
    edtCodigo.value = codigoCliente.toString()
    lblNomeCleinte.value = parcela?.nome ?: ""
    dataProviderParcelas.items.clear()
    dataProviderParcelas.items.addAll(lista)
    dataProviderParcelas.refreshAll()
    setValorTotal(0.00)
  }
  
  override fun imprimeBoletos(contratos: List<Contrato>) {
    ViewBoletoHelp.showBoleto(contratos)
  }
  
  private fun setValorTotal(valor: Double) {
    val decimalFormat = DecimalFormat("#,##0.00")
    lblValorTotal.value = decimalFormat.format(valor)
  }
  
  private fun DadosPagador.podeSelecionar() = this.statusParcela == 0
}