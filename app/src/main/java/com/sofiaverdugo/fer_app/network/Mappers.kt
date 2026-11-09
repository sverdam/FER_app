package com.sofiaverdugo.fer_app.network

import com.sofiaverdugo.fer_app.models.Event
import com.sofiaverdugo.fer_app.models.Post
import com.sofiaverdugo.fer_app.models.PostAttachment
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

private fun List<TagDto>?.slugs(): Set<String> =
    orEmpty().mapNotNull { it.slug?.trim()?.lowercase() }.toSet()
private fun String.toAbsoluteUrl(): String =
    if (startsWith("http://") || startsWith("https://")) {
        this
    } else {
        ApiConfig.HOST.trimEnd('/') + "/" + trimStart('/')
    }

private fun MediaDto?.absoluteUrl(): String? =
    this?.url?.takeIf { it.isNotBlank() }?.toAbsoluteUrl()

private fun String?.toLocalDateTime(): LocalDateTime? {
    if (this == null) return null
    return try {
        OffsetDateTime.parse(this)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        null
    }
}

private fun AttachmentDto.toAttachment(index: Int): PostAttachment? {
    val attachmentId = id?.toString() ?: index.toString()
    val mediaUrl = media.absoluteUrl()
    val externalUrl = url?.takeIf { it.isNotBlank() }

    return when (type) {
        "image" -> mediaUrl?.let {
            PostAttachment.Image(
                id = attachmentId,
                url = it,
                width = media?.width,
                height = media?.height
            )
        }

        "video" -> (mediaUrl ?: externalUrl)?.let {
            PostAttachment.Video(
                id = attachmentId,
                url = it,
                thumbnailUrl = thumbnail.absoluteUrl()
            )
        }

        "link" -> externalUrl?.let {
            PostAttachment.Link(
                id = attachmentId,
                url = it,
                title = title,
                description = description
            )
        }

        "file" -> mediaUrl?.let {
            PostAttachment.File(
                id = attachmentId,
                url = it,
                name = media?.name ?: title ?: "Archivo",
                mimeType = media?.mime ?: "application/octet-stream",
                size = media?.size?.let { kb -> (kb * 1024).toLong() }
            )
        }

        else -> null
    }
}

fun PostDto.toModel(): Post? {
    val postId = id ?: return null
    val published = publishedAt.toLocalDateTime()
        ?: createdAt.toLocalDateTime()
        ?: LocalDateTime.now()
    val updated = updatedAt.toLocalDateTime()
    val wasEdited = updated != null && Duration.between(published, updated).toMinutes() >= 5

    return Post(
        id = postId,
        title = title?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        body = body.orEmpty(),
        thumbnail = thumbnail.absoluteUrl(),
        attachments = attachments.orEmpty().mapIndexedNotNull { index, dto ->
            dto.toAttachment(index)
        },
        authorId = 0,
        createdAt = published,
        updatedAt = if (wasEdited) updated else null ,
        audiences = audiences.slugs(),
        levels = levels.slugs()
    )
}

fun EventDto.toModel(): Event? {
    val eventId = id ?: return null
    val eventTitle = title ?: return null
    val start = dateTime.toLocalDateTime() ?: return null
    val published = publishedAt.toLocalDateTime()
        ?: createdAt.toLocalDateTime()
        ?: LocalDateTime.now()

    return Event(
        id = eventId,
        title = eventTitle,
        description = description.orEmpty(),
        location = location.orEmpty(),
        address = address?.takeIf { it.isNotBlank() },
        dateTime = start,
        endDateTime = endDateTime.toLocalDateTime(),
        cost = cost.orEmpty(),
        registerLink = registerLink.orEmpty(),
        photo = photo.absoluteUrl().orEmpty(),
        createdAt = published,
        audiences = audiences.slugs(),
        levels = levels.slugs()
    )
}