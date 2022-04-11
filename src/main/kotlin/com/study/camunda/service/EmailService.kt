package com.study.camunda.service

interface EmailService {
    fun sendMail(email: String, attachmentFile: String)
}