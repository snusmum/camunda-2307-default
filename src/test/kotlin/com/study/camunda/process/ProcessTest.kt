package com.study.camunda.process

import com.study.camunda.common.TARGET_EMAIL_VAR_NAME
import com.study.camunda.delegate.CatFactDelegate
import com.study.camunda.delegate.EmailDelegate
import com.study.camunda.delegate.PictureDelegate
import com.study.camunda.delegate.WatermarkDelegate
import com.study.camunda.service.CatFactsService
import com.study.camunda.service.EmailService
import com.study.camunda.service.PictureService
import com.study.camunda.service.WatermarkService
import org.assertj.core.data.MapEntry.entry
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.InOrder
import org.mockito.kotlin.*

@Deployment(resources = ["bpmn/2307.bpmn"])
class ProcessTest {

    companion object {
        @RegisterExtension
        @JvmField
        val extension: ProcessEngineExtension = ProcessEngineExtension.builder()
            .useProcessEngine(StandaloneInMemProcessEngineConfiguration().buildProcessEngine())
            .build()
    }


    private val catFactsService: CatFactsService = mock()
    private val pictureService: PictureService = mock()
    private val watermarkService: WatermarkService = mock()
    private val emailService: EmailService = mock()

    private val email: String = "тест мыл"
    private val fact = "котэ"
    private val pict = "картинко"

    @BeforeEach
    fun setup() {
        Mocks.register("catFactDelegate", CatFactDelegate(catFactsService))
        Mocks.register("pictureDelegate", PictureDelegate(pictureService))
        Mocks.register("watermarkDelegate", WatermarkDelegate(watermarkService))
        Mocks.register("emailDelegate", EmailDelegate(emailService))

        whenever(catFactsService.getFact()).thenReturn(fact)
        whenever(pictureService.getPicture()).thenReturn(pict)
    }

    @AfterEach
    fun teardown() {
        Mocks.reset()
    }

    @Test
    fun `green way`() {
        // Act
        val pi = startProcess()

        // Assert
        assertThat(pi).isEnded

        val inOrder: InOrder = inOrder(watermarkService, emailService)
        inOrder.verify(watermarkService).textToImage(fact, pict)
        inOrder.verify(emailService).sendMail(email, pict)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `send email by timer after first fail`() {
        // Arrange
        val errorMessage = "mail error"
        whenever(emailService.sendMail(any(), any())).doThrow(RuntimeException(errorMessage)).then {}

        // Act #1
        val pi = startProcess()

        // Assert #1
        assertThat(pi).isWaitingAt("resendEmailTimer")
        assertThat(pi).variables().contains(entry("errorMessage", errorMessage))

        // Act #2
        execute(job())

        // Assert #2
        assertThat(pi).isEnded

        val inOrder: InOrder = inOrder(watermarkService, emailService)
        inOrder.verify(watermarkService).textToImage(fact, pict)
        inOrder.verify(emailService, times(2)).sendMail(email, pict)
        inOrder.verifyNoMoreInteractions()
    }

    private fun startProcess(): ProcessInstance {
        val pi = extension.processEngine
            .runtimeService
            .startProcessInstanceByKey(
                "study_camunda_2307_process",
                withVariables(TARGET_EMAIL_VAR_NAME, email)
            )
        assertThat(pi).isNotNull
        return pi
    }

}