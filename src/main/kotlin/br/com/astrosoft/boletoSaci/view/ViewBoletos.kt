package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.DadosPagador
import br.com.astrosoft.boletoSaci.view.ViewBoletoHelp.Companion.showBoleto
import br.com.astrosoft.boletoSaci.viewmodel.IViewModelBoletos
import br.com.astrosoft.boletoSaci.viewmodel.ViewModelBoletos
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.addColumnBool
import br.com.astrosoft.framework.view.addColumnDate
import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.framework.view.resource.resourceTxt
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.navigateToView
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.value.ValueChangeMode.EAGER
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinSession

@Route(value = "boletos", layout = MainAppLayout::class)
@Caption("Boletos")
@Icon(VaadinIcon.BARCODE)
@CssImport("frontend://styles/grid.css", themeFor = "vaadin-grid")
class ViewBoletos: IViewModelBoletos, ViewLayout<ViewModelBoletos>() {
  private var gridParcelas: Grid<DadosPagador>
  private val dataProviderParcelas = ListDataProvider<DadosPagador>(mutableListOf())
  override val viewModel = ViewModelBoletos(this)
  
  init {
    horizontalLayout {
      setWidthFull()
      
      horizontalLayout {
        isExpand = true
        setWidthFull()
        justifyContentMode = JustifyContentMode.END
        button("Adicionar Boleto") {
          icon = VaadinIcon.PLUS.create()
          addThemeVariants(LUMO_PRIMARY)
          onLeftClick {
            viewModel.adicionarBoleto()
          }
        }
        button("Gera Remessa") {
          icon = VaadinIcon.BARCODE.create()
          addThemeVariants(LUMO_SUCCESS)
          onLeftClick {
            viewModel.gerarRemessa()
          }
        }
      }
    }
    gridParcelas = grid(dataProviderParcelas) {
      isExpand = true
      isMultiSort = true
      addThemeVariants(LUMO_COMPACT)
      this.appendHeaderRow()
      val filterRow = this.appendHeaderRow()
      val edtCodigo = NumberField().apply {
        addValueChangeListener {event ->
          dataProviderParcelas.clearFilters()
          val value = event.value?.toInt()
          if(value != null)
            dataProviderParcelas.addFilter {dados ->
              dados.codigo.toString().startsWith(value.toString())
            }
        }
        this.width = "100px"
        this.valueChangeMode = EAGER
        this.setSizeFull()
        this.placeholder = "Filtro"
      }
      val edtContrato = NumberField().apply {
        addValueChangeListener {event ->
          dataProviderParcelas.clearFilters()
          val value = event.value?.toInt()
          if(value != null)
            dataProviderParcelas.addFilter {dados ->
              dados.contrno.toString().startsWith(value.toString())
            }
        }
        this.width = "150px"
        this.valueChangeMode = EAGER
        this.setSizeFull()
        this.placeholder = "Filtro"
      }
      
      addColumnInt(DadosPagador::codigo) {
        setHeader("Código")
        filterRow.getCell(this)
          .setComponent(edtCodigo)
        isAutoWidth = false
      }
      addColumnString(DadosPagador::nome) {
        setHeader("Nome")
      }
      addColumnInt(DadosPagador::storeno) {
        setHeader("Lj")
      }
      addColumnInt(DadosPagador::contrno) {
        setHeader("Contrato")
        filterRow.getCell(this)
          .setComponent(edtContrato)
        isAutoWidth = false
      }
      addColumnInt(DadosPagador::instno) {
        setHeader("Pr")
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
      addColumnInt(DadosPagador::nossoNumero) {
        setHeader("Nosso Número")
      }
      this.setClassNameGenerator {
        null
      }
      
      this.columns.forEach {
        it.isSortable = false
      }
      
      viewModel.updateGrid()
    }
  }
  
  override fun updateGrid(list: List<DadosPagador>) {
    dataProviderParcelas.items.clear()
    dataProviderParcelas.items.addAll(list)
    dataProviderParcelas.refreshAll()
  }
  
  override fun openAdicionaParcelas() {
    ViewPesquisaParcelas.navigate()
  }
  
  override fun openText(arquivoStr: String) {
    val resource = resourceTxt(arquivoStr)
    val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)
    UI.getCurrent().page.executeJs("window.open($0, $1)", registration.resourceUri.toString(), "_blank")
    
    showBoleto(viewModel.boletosGerados())
  }
  
  companion object {
    fun navigate() {
      navigateToView(ViewBoletos::class)
    }
  }
}