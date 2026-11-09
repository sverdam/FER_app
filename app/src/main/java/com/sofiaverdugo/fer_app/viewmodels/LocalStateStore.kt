package com.sofiaverdugo.fer_app.viewmodels

import android.content.Context

class LocalStateStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("fer_user_state", Context.MODE_PRIVATE)

    fun getIds(key: String): Set<Int> =
        prefs.getStringSet(key, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun saveIds(key: String, ids: Set<Int>) {
        prefs.edit().putStringSet(key, ids.map { it.toString() }.toSet()).apply()
    }

    companion object {
        const val LIKED_POSTS = "liked_posts"
        const val LIKED_EVENTS = "liked_events"
        const val INTERESTED_EVENTS = "interested_events"
    }
}