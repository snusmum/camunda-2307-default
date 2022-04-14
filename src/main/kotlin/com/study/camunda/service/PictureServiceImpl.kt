package com.study.camunda.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Service
class PictureServiceImpl(
    @Value("\${source.picture.url}") val pictureUrl: String,
    @Value("\${defaults.picture.file}") val defaultPictureFile: String
) : PictureService {

    override fun getPicture(): String {
        return try {
            toTempFile(URL(pictureUrl).openStream())
        } catch (ex: Exception) {
            ex.printStackTrace()
            toTempFile(ClassPathResource(defaultPictureFile).inputStream)
        }
    }

    private fun toTempFile(input: InputStream): String {
        val tempFile = File.createTempFile("temp-", "-2307.jpg")
        input.use { Files.copy(it, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING) }
        return tempFile.toPath().toString()
    }

}