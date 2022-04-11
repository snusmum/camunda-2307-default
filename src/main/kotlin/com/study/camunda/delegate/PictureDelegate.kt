package com.study.camunda.delegate

import com.study.camunda.service.PictureService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class PictureDelegate(private val pictureService: PictureService) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val fileName = pictureService.getPicture()
        execution.setVariable("pictureFileName", fileName)
    }

}