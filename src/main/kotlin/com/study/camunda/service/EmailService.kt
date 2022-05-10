package com.study.camunda.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.sender.email}") private val senderEmail: String,
) {

    fun sendMail(email: String, attachmentFile: String) {

        val helper = MimeMessageHelper(javaMailSender.createMimeMessage(), true, "UTF-8")

        helper.setFrom(senderEmail)
        helper.setTo(email)
        helper.setSubject("Факты о котиках (Cat facts)")
        helper.setText("Всё, что нужно знать - во вложении.")

        val file = File(attachmentFile)
        helper.addAttachment(file.name, file)

        javaMailSender.send(helper.mimeMessage)

    }

}