package com.study.camunda.api

import com.study.camunda.common.CAT_FACT_PROCESS_KEY
import com.study.camunda.common.TARGET_EMAIL_VAR_NAME
import com.study.camunda.service.CamundaService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PublicController(private val camundaService: CamundaService) {

    @PostMapping("/send_fact/{email}")
    fun sendCatFact(@PathVariable(name = "email") targetEmail: String) {
        camundaService.startProcess(CAT_FACT_PROCESS_KEY, mapOf(TARGET_EMAIL_VAR_NAME to targetEmail))
    }

}