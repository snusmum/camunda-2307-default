package com.study.camunda.delegate

import com.study.camunda.common.CAT_FACT_VAR_NAME
import com.study.camunda.service.CatFactsService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CatFactDelegate(private val catFactsService: CatFactsService) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val fact = catFactsService.getFact()
        execution.setVariable(CAT_FACT_VAR_NAME, fact)
    }

}