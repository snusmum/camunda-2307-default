package com.study.camunda.api

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest
import com.jupiter.tools.spring.test.web.annotation.EnableRestTest
import com.study.camunda.feign.CatFactsClient
import org.apache.commons.io.IOUtils
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.awaitility.Awaitility.await
import org.awaitility.Durations.ONE_MINUTE
import org.awaitility.Durations.ONE_SECOND
import org.camunda.bpm.engine.test.Deployment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

@EnableRestTest
@EnablePostgresIntegrationTest
@ExtendWith(SoftAssertionsExtension::class)
@Deployment(resources = ["bpmn/2307.bpmn"])
internal class PublicControllerWithoutRemotesIT @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @RegisterExtension
    var greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(
        GreenMailConfiguration.aConfig().withUser("test@test.ru", "test")
    )

    @MockBean
    private lateinit var catFactClient: CatFactsClient

    private val email: String = "test@mail.ru"

    @Test
    fun `send default values when remotes unavailable`(softly: SoftAssertions) {
        // Act
        mockMvc.post("/api/send_fact/${email}")
            .andExpect {
                status { isOk() }
            }

        // Assert
        verify(catFactClient).getFact()

        await().pollDelay(ONE_SECOND)
            .atMost(ONE_MINUTE)
            .until { greenMail.receivedMessages.isNotEmpty() }

        val message = greenMail.receivedMessages[0]
        softly.assertThat(message.getRecipients(Message.RecipientType.TO))
            .containsExactly(InternetAddress(email))

        softly.assertThat(
            IOUtils.contentEquals(
                ((message.content as MimeMultipart).getBodyPart(1) as MimeBodyPart).inputStream,
                ClassPathResource("default_fact_picture.jpg").inputStream
            )
        ).isTrue
    }

}