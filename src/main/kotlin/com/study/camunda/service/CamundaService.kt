package com.study.camunda.service

import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Service

@Service
class CamundaService(private val runtimeService: RuntimeService) {

    fun startProcess(processKey: String, variables: Map<String, Any?>) {
        runtimeService.startProcessInstanceByKey(processKey, variables);
    }

}