package br.com.astrosoft.framework.util

import java.io.File
import java.io.FileReader
import java.util.*

open class ConfigFile {
  private val propertieFile = System.getProperty("ebean.props.file")
  
  private fun properties(): Properties {
    val properties = Properties()
    val file = File(propertieFile)
    
    properties.load(FileReader(file))
    return properties
  }
  
  private val properties = properties()
  
  fun getValue(key: String) = properties[key].toString() ?: ""
  
  companion object {
    private val conf = ConfigFile()
    val test = conf.getValue("test") == "true"
    val usernameMail = conf.getValue("mail.username")
    val passwordMail = conf.getValue("mail.password")
  }
}