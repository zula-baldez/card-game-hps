package com.example.avatarsservice.service

import com.example.common.dto.Avatar
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.min

@Component
class AvatarProcessingServiceImpl(
    private val storage: AvatarsStorage
) : AvatarProcessingService {
    private val imageSizePixels = 100

    override fun processAvatar(accountId: Long, file: MultipartFile): Avatar {
        val image = ImageIO.read(file.inputStream)
        val minSize = min(image.width, image.height)

        val cutImage = image.getSubimage(0, 0, minSize, minSize)
        val resizedImage = BufferedImage(imageSizePixels, imageSizePixels, BufferedImage.TYPE_INT_RGB)
        val graphics = resizedImage.graphics
        graphics.drawImage(cutImage, 0, 0, imageSizePixels, imageSizePixels, null)
        graphics.dispose()

        val buffer = ByteArrayOutputStream()
        ImageIO.write(resizedImage, "jpg", buffer)
        buffer.flush()
        buffer.close()

        val url = storage.storeFile("user-avatar-$accountId-${System.currentTimeMillis()}", buffer.toByteArray())

        return Avatar(accountId, url)
    }

}