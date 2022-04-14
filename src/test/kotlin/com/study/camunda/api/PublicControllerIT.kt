package com.study.camunda.api

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest
import com.jupiter.tools.spring.test.web.annotation.EnableRestTest
import com.study.camunda.feign.CatFactsClient
import com.study.camunda.model.CatFact
import org.apache.commons.io.IOUtils
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.awaitility.Awaitility.await
import org.awaitility.Durations.ONE_MINUTE
import org.awaitility.Durations.ONE_SECOND
import org.camunda.bpm.engine.test.Deployment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart


@EnableRestTest
@EnablePostgresIntegrationTest
@ExtendWith(SoftAssertionsExtension::class)
@Deployment(resources = ["bpmn/2307.bpmn"])
internal class PublicControllerIT @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @RegisterExtension
    var greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(
        GreenMailConfiguration.aConfig().withUser("test@test.ru", "test")
    )

    @MockBean
    private lateinit var catFactClient: CatFactsClient

    private val email: String = "test@mail.ru"

    var factoryBackup: URLStreamHandlerFactory? = null

    @Mock
    lateinit var urlFactory: URLStreamHandlerFactory

    @BeforeEach
    fun mockUrlFactory() {
        factoryBackup = ReflectionTestUtils.getField(URL::class.java, "factory") as URLStreamHandlerFactory?
        URL.setURLStreamHandlerFactory(urlFactory)
    }

    @AfterEach
    fun restoreUrlFactory() {
        ReflectionTestUtils.setField(URL::class.java, "factory", factoryBackup)
    }

    @Test
    fun `send downloaded fact on downloaded picture`(softly: SoftAssertions) {
        // Arrange
        whenever(catFactClient.getFact()).thenReturn(CatFact("Yes or now, cat or cow", 22))
        whenever(urlFactory.createURLStreamHandler("http")).thenReturn(MockHttpHandler())

        // Act
        mockMvc.post("/api/send_fact/${email}")
            .andExpect {
                status { isOk() }
            }

        // Assert
        await().pollDelay(ONE_SECOND)
            .atMost(ONE_MINUTE)
            .until { greenMail.receivedMessages.isNotEmpty() }

        val message = greenMail.receivedMessages[0]
        softly.assertThat(message.getRecipients(Message.RecipientType.TO))
            .containsExactly(InternetAddress(email))

        softly.assertThat(
            IOUtils.contentEquals(
                ((message.content as MimeMultipart).getBodyPart(1) as MimeBodyPart).inputStream,
                ClassPathResource("custom_fact_picture.jpg").inputStream
            )
        ).isTrue
    }

    class MockHttpHandler: URLStreamHandler() {
        override fun openConnection(u: URL?): URLConnection {
            val urlConnection = mock<URLConnection>()
            whenever(urlConnection.getInputStream()).thenReturn(ClassPathResource("custom_picture.jpg").inputStream)
            return urlConnection
        }

    }

}