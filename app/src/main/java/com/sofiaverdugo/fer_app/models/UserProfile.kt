package com.sofiaverdugo.fer_app.models

data class UserProfile(
    val audience: String? = null,
    val level: String? = null
)

private fun Set<String>.allows(value: String?) =
    isEmpty() || value == null || value in this

fun matchesProfile(audiences: Set<String>, levels: Set<String>, profile: UserProfile) =
    audiences.allows(profile.audience) && levels.allows(profile.level)