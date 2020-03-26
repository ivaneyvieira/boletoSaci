package br.com.astrosoft.boletoSaci.view

import br.com.astrosoft.boletoSaci.viewmodel.IViewRelatorio
import br.com.astrosoft.boletoSaci.viewmodel.ViewModelRelatorio
import br.com.astrosoft.framework.view.ConverteByte
import br.com.astrosoft.framework.view.PDFViewer
import br.com.astrosoft.framework.view.ViewLayout
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalTime

@Route(value = "crediario", layout = MainAppLayout::class)
@Caption("Gerar Boleto")
@Icon(VaadinIcon.BARCODE)
class ViewRelatorio: IViewRelatorio, ViewLayout<ViewModelRelatorio>() {
  private var areaView: VerticalLayout
  private lateinit var edtParcela: TextField
  private lateinit var edtContrato: TextField
  private lateinit var edtLoja: TextField
  override val viewModel = ViewModelRelatorio(this)
  
  init {
    horizontalLayout {
      edtLoja = textField("Loja") {
        width = "100px"
        value = "1"
      }
      edtContrato = textField("Contrato") {
        width = "200px"
        value = "130976315"
      }
      edtParcela = textField("Parcela") {
        width = "100px"
        value = "1"
      }
      button("Gera Boleto") {

        onLeftClick {
          viewModel.geraBoleto()
        }
      }
    }
    areaView = verticalLayout {
      setSizeFull()
    }
  }
  
  override val loja: Int?
    get() = edtLoja.value?.toIntOrNull()
  override val contrato: Int?
    get() = edtContrato.value?.toIntOrNull()
  override val parcela: Int?
    get() = edtParcela.value?.toIntOrNull()
  
  override fun updateBoleto(bytesBoletos: ByteArray) {
    areaView.removeAll()
    val timeNumber =  System.currentTimeMillis()
    val resource = StreamResource("$timeNumber.pdf", ConverteByte(bytesBoletos))
    val pdfView = PDFViewer(resource)
    areaView.addAndExpand(pdfView)
  }

}

