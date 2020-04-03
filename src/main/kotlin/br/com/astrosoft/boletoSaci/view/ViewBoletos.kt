package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.model.DadosBoleto
import br.com.astrosoft.boletoSaci.view.ViewBoletoHelp.Companion.showArquivoRemessaBrowser
import br.com.astrosoft.boletoSaci.view.ViewBoletoHelp.Companion.showBoleto
import br.com.astrosoft.boletoSaci.viewmodel.IViewModelBoletos
import br.com.astrosoft.boletoSaci.viewmodel.ViewModelBoletos
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.addColumnDate
import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.navigateToView
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.value.ValueChangeMode.EAGER
import com.vaadin.flow.router.Route

@Route(value = "boletos", layout = MainAppLayout::class)
@Caption("Boletos")
@Icon(VaadinIcon.BARCODE)
//@CssImport("frontend://styles/grid.css", themeFor = "vaadin-grid")
class ViewBoletos: IViewModelBoletos, ViewLayout<ViewModelBoletos>() {
  private var gridParcelas: Grid<DadosBoleto>
  private val dataProviderParcelas = ListDataProvider<DadosBoleto>(mutableListOf())
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
              dados.codigo.toString()
                .startsWith(value.toString())
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
              dados.contrno.toString()
                .startsWith(value.toString())
            }
        }
        this.width = "150px"
        this.valueChangeMode = EAGER
        this.setSizeFull()
        this.placeholder = "Filtro"
      }
  
      addColumnInt(DadosBoleto::codigo) {
        setHeader("Código")
        filterRow.getCell(this)
          .setComponent(edtCodigo)
        isAutoWidth = false
      }
      addColumnString(DadosBoleto::nome) {
        setHeader("Nome")
      }

      addColumnInt(DadosBoleto::contrno) {
        setHeader("Contrato")
        filterRow.getCell(this)
          .setComponent(edtContrato)
        isAutoWidth = false
      }
  
      addColumnDate(DadosBoleto::localDtVencimento) {
        setHeader("Vencimento")
      }
      addColumnDouble(DadosBoleto::valorParcela) {
        setHeader("Valor")
      }
      addColumnDouble(DadosBoleto::valorJuros) {
        setHeader("Valor")
      }
      addColumnDouble(DadosBoleto::valorTotal) {
        setHeader("Total")
      }
      addColumnInt(DadosBoleto::nossoNumero) {
        setHeader("Nosso Número")
      }
      addComponentColumn {dados ->
        Button().apply {
          //width = "60px"
          icon = VaadinIcon.BARCODE.create()
          addClickListener {
            if(dados.processado)
              showBoleto(listOf(dados))
            else
              showWarning("Este boleto não foi processado para gera aquivo de remessa")
          }
        }
      }.apply {
        isAutoWidth = true
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
  
  override fun updateGrid(list: List<DadosBoleto>) {
    dataProviderParcelas.items.clear()
    dataProviderParcelas.items.addAll(list)
    dataProviderParcelas.refreshAll()
  }
  
  override fun openAdicionaParcelas() {
    ViewPesquisaParcelas.navigate()
  }
  
  override fun openText(dadosBoleto: List<DadosBoleto>) {
    showArquivoRemessaBrowser(dadosBoleto)
    showBoleto(dadosBoleto)
  }
  
  companion object {
    fun navigate() {
      navigateToView(ViewBoletos::class)
    }
  }
}