package com.study.camunda.delegate

import com.study.camunda.common.PICTURE_FILE_VAR_NAME
import com.study.camunda.common.TARGET_EMAIL_VAR_NAME
import com.study.camunda.service.EmailService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class EmailDelegate(private val emailService: EmailService): JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val targetEmail = execution.getVariable(TARGET_EMAIL_VAR_NAME)!!.toString()
        val pictureFileName = execution.getVariable(PICTURE_FILE_VAR_NAME)!!.toString()
        emailService.sendMail(targetEmail, pictureFileName);
    }

}