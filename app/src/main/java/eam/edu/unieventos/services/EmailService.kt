package eam.edu.unieventos.services

import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService {
    private val username = "contreras.joseph.7630@eam.edu.co"
    private val password = "lgcp znhk zfom srvj"

    fun sendEmail(to: String, subject: String, body: String) {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val session = Session.getInstance(props,
            object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
            )
            message.subject = subject
            message.setText(body)

            Transport.send(message)
            println("Correo enviado correctamente")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}