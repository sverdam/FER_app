package com.sofiaverdugo.fer_app.models

import java.time.LocalDateTime

data class Post(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val body: String,
    val thumbnail: String? = null,
    val attachments: List<PostAttachment> = emptyList(),
    val authorId: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val likes: Int = 0,
    val hasLiked: Boolean = false,
    val audiences: Set<String> = emptySet(),
    val levels: Set<String> = emptySet()
)

sealed class PostAttachment {

    data class Image(
        val id: String,
        val url: String,
        val width: Int? = null,
        val height: Int? = null,
        val thumbnailUrl: String? = null
    ) : PostAttachment()

    data class Video(
        val id: String,
        val url: String,
        val thumbnailUrl: String? = null,
        val duration: Long? = null
    ) : PostAttachment()

    data class Link(
        val id: String,
        val url: String,
        val title: String? = null,
        val description: String? = null,
        val thumbnailUrl: String? = null
    ) : PostAttachment()

    data class File(
        val id: String,
        val url: String,
        val name: String,
        val mimeType: String,
        val size: Long? = null
    ) : PostAttachment()
}