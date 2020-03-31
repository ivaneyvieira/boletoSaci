package br.com.astrosoft.framework.view.resource

import com.helger.commons.io.stream.StringInputStream
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.InputStream
import java.nio.charset.Charset

class ResourceTxt(val text: String): InputStreamFactory {
  override fun createInputStream(): InputStream {
    return StringInputStream(text, Charset.defaultCharset())
  }
}

fun resourceTxt(text: String): StreamResource {
  val timeNumber = System.currentTimeMillis()
  return StreamResource("$timeNumber.txt", ResourceTxt(text))
}