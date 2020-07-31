package user

import javax.mail._
import javax.mail.internet._
import java.util.Date
import java.util.Properties
import javax.mail.Session

object MailAgent {
  def start(to: String,
            from: String,
            subject: String,
            content: String,
            smtpHost: String) = new MailAgent(to, from, subject, content, smtpHost)
}

class MailAgent(to: String,
                from: String,
                subject: String,
                content: String,
                smtpHost: String)
{
  var message: Message = null

  message = createMessage
  message.setFrom(new InternetAddress(from))
  setToCcBccRecipients()

  message.setSentDate(new Date())
  message.setSubject(subject)
  message.setText(content)

  // throws MessagingException
  def sendMessage(): Unit = {
    Transport.send(message)
  }

  def createMessage: Message = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "587")
    val session = Session.getInstance(props, new Authenticator() {
      override protected def getPasswordAuthentication = new PasswordAuthentication("kerbezorazgaliyeva@gmail.com", "habittracker")
    })
    new MimeMessage(session)
  }

  // throws AddressException, MessagingException
  def setToCcBccRecipients(): Unit = {
    setMessageRecipients(to, Message.RecipientType.TO)
  }

  // throws AddressException, MessagingException
  def setMessageRecipients(recipient: String, recipientType: Message.RecipientType) {
    // had to do the asInstanceOf[...] call here to make scala happy
    val addressArray = buildInternetAddressArray(recipient).asInstanceOf[Array[Address]]
    if ((addressArray != null) && (addressArray.length > 0))
    {
      message.setRecipients(recipientType, addressArray)
    }
  }

  // throws AddressException
  def buildInternetAddressArray(address: String): Array[InternetAddress] = {
    // could test for a null or blank String but I'm letting parse just throw an exception
    InternetAddress.parse(address)
  }

}