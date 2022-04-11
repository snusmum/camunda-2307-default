package com.study.camunda.service

import org.springframework.stereotype.Service
import java.awt.*
import java.awt.font.GlyphVector
import java.awt.font.TextAttribute
import java.awt.image.BufferedImage
import java.io.File
import java.text.AttributedString
import javax.imageio.ImageIO
import kotlin.math.min

@Service
class WatermarkServiceImpl : WatermarkService {

    override fun textToImage(text: String, imagePath: String) {
        val file = File(imagePath)

        val image: BufferedImage = ImageIO.read(file)
        val font = Font("Arial", Font.PLAIN, 18)
        val graphics: Graphics = image.graphics
        val metrics: FontMetrics = graphics.getFontMetrics(font)

        val textLines = textToLines(text, font, metrics, image)
        drawText(textLines, font, image, graphics)

        ImageIO.write(image, "jpg", file)
    }

    fun textToLines(text: String, font: Font, metrics: FontMetrics, image: BufferedImage): MutableList<String> {
        val vector: GlyphVector = font.createGlyphVector(metrics.fontRenderContext, text)
        val outline: Shape = vector.getOutline(0f, 0f)
        val expectedWidth: Double = outline.bounds.getWidth()
        val textFits = image.width >= expectedWidth

        val textLines: MutableList<String> = mutableListOf()

        if (textFits) {
            textLines.add(text)
        } else {
            val parts: Int = (expectedWidth / image.width).toInt() + 1
            val partLength: Int = text.length / parts
            for(i in 0 until parts) {
                textLines.add(text.substring(i * partLength, min(text.length, partLength * (i + 1))))
            }
        }

        return textLines
    }

    fun drawText(textLines: List<String>, font: Font, image: BufferedImage, graphics: Graphics) {
        val positionX = 0
        var positionY = image.height / 2

        for (t in textLines) {
            val attributedText = AttributedString(t)
            attributedText.addAttribute(TextAttribute.FONT, font)
            attributedText.addAttribute(TextAttribute.FOREGROUND, Color.DARK_GRAY)
            attributedText.addAttribute(TextAttribute.BACKGROUND, Color.WHITE)
            graphics.drawString(attributedText.iterator, positionX, positionY)
            positionY += 20
        }
        graphics.dispose()
    }

}