package com.study.camunda.delegate

import com.study.camunda.common.CAT_FACT_VAR_NAME
import com.study.camunda.common.PICTURE_FILE_VAR_NAME
import com.study.camunda.service.WatermarkService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class WatermarkDelegate(private val watermarkService: WatermarkService): JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val catFact = execution.getVariable(CAT_FACT_VAR_NAME)!!.toString()
        val pictureFileName = execution.getVariable(PICTURE_FILE_VAR_NAME)!!.toString()
        watermarkService.textToImage(catFact, pictureFileName)
    }

}