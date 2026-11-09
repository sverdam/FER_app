package com.sofiaverdugo.fer_app.models

import java.time.LocalDateTime

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val address: String? = null,
    val dateTime: LocalDateTime,
    val endDateTime: LocalDateTime? = null,
    val cost: String,
    val registerLink: String,
    val peopleInterested: Int = 0,
    val isInterested: Boolean = false,
    val likes: Int = 0,
    val hasLiked: Boolean = false,
    val photo: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val audiences: Set<String> = emptySet(),
    val levels: Set<String> = emptySet()
)