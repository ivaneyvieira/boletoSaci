package br.com.astrosoft.framework.view

import com.github.mvysny.karibudsl.v10.anchor
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import org.vaadin.olli.FileDownloadWrapper
import java.io.ByteArrayInputStream
import java.io.InputStream

class SubWindowPDF(bytesBoletos: ByteArray): Dialog() {
  init {
    width = "1200px"
    height = "500px"
    val timeNumber = System.currentTimeMillis()
    val resourcePDF = StreamResource("$timeNumber.pdf", ConverteByte(bytesBoletos))
    val buttonWrapper = FileDownloadWrapper(resourcePDF)

    verticalLayout {
      horizontalLayout {
        add(Anchor(resourcePDF, "Download"))
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          close()
        }
      }
      
      addAndExpand(PDFViewer(resourcePDF))
    }
    isCloseOnEsc = true
  }
}

class ConverteByte(val bytesBoletos: ByteArray): InputStreamFactory {
  override fun createInputStream(): InputStream {
    return ByteArrayInputStream(bytesBoletos)
  }
}