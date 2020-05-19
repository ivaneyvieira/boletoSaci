package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.util.ConfigFile
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class Gmail {
  fun sendMail(to: String, subject: String, htmlMessage: String, filename: String? = null): Boolean {
    val username = ConfigFile.usernameMail
    val password = ConfigFile.passwordMail
    val prop = Properties()
    prop.put("mail.smtp.host", "smtp.gmail.com")
    prop.put("mail.smtp.port", "465")
    prop.put("mail.smtp.auth", "true")
    prop.put("mail.smtp.socketFactory.port", "465")
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    val session: Session = Session.getInstance(prop, GmailAuthenticator(username, password))
    
    try {
      val message: Message = MimeMessage(session)
      message.setFrom(InternetAddress(username))
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
      message.subject = subject
      val multPart = MimeMultipart()
      //Text
      val partText = MimeBodyPart()
      partText.dataHandler = DataHandler((HTMLDataSource(htmlMessage)))
      multPart.addBodyPart(partText)
      //File
      if(filename != null) {
        val partFile = MimeBodyPart()
        val fileDataSource = FileDataSource(filename)
        partFile.dataHandler = DataHandler(fileDataSource)
        partFile.fileName = fileDataSource.name
        multPart.addBodyPart(partFile)
      }

      message.setContent(multPart)
      
      Transport.send(message)
      return true
    } catch(e: Throwable) {
      e.printStackTrace()
      return false
    }
  }
}

internal class HTMLDataSource(private val html: String?): DataSource {
  @Throws(IOException::class)
  override fun getInputStream(): InputStream {
    if(html == null) throw IOException("html message is null!")
    return ByteArrayInputStream(html.toByteArray())
  }
  
  @Throws(IOException::class)
  override fun getOutputStream(): OutputStream {
    throw IOException("This DataHandler cannot write HTML")
  }
  
  override fun getContentType(): String {
    return "text/html"
  }
  
  override fun getName(): String {
    return "HTMLDataSource"
  }
}

class GmailAuthenticator(val username: String, val password: String): Authenticator() {
  override fun getPasswordAuthentication(): PasswordAuthentication {
    return PasswordAuthentication(username, password)
  }
}
