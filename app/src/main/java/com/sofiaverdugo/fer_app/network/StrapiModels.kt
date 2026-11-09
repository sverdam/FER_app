package com.sofiaverdugo.fer_app.network

data class StrapiListResponse<T>(
    val data: List<T>? = null
)

data class MediaDto(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val mime: String? = null,
    val size: Double? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class AttachmentDto(
    val id: Int? = null,
    val type: String? = null,
    val media: MediaDto? = null,
    val thumbnail: MediaDto? = null,
    val url: String? = null,
    val title: String? = null,
    val description: String? = null
)

data class PostDto(
    val id: Int? = null,
    val documentId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val body: String? = null,
    val thumbnail: MediaDto? = null,
    val attachments: List<AttachmentDto>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val publishedAt: String? = null,
    val audiences: List<TagDto>? = null,
    val levels: List<TagDto>? = null
)

data class EventDto(
    val id: Int? = null,
    val documentId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val address: String? = null,
    val dateTime: String? = null,
    val endDateTime: String? = null,
    val cost: String? = null,
    val registerLink: String? = null,
    val photo: MediaDto? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val publishedAt: String? = null,
    val audiences: List<TagDto>? = null,
    val levels: List<TagDto>? = null
)

data class TagDto(
    val id: Int? = null,
    val name: String? = null,
    val slug: String? = null
)