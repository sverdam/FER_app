package com.sofiaverdugo.fer_app.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sofiaverdugo.fer_app.models.Event
import com.sofiaverdugo.fer_app.network.RetrofitClient
import com.sofiaverdugo.fer_app.network.toModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventVM(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.eventsApi
    private val store = LocalStateStore(application)
    private var likedIds: Set<Int> = store.getIds(LocalStateStore.LIKED_EVENTS)
    private var interestedIds: Set<Int> = store.getIds(LocalStateStore.INTERESTED_EVENTS)

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val likedEvents = events
        .map { list -> list.filter { it.hasLiked } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val interestedEvents = events
        .map { list -> list.filter { it.isInterested } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _events.value = api.getEvents().data.orEmpty()
                    .mapNotNull { it.toModel() }
                    .map { event ->
                        val liked = event.id in likedIds
                        val interested = event.id in interestedIds
                        event.copy(
                            hasLiked = liked,
                            likes = if (liked) 1 else 0,
                            isInterested = interested,
                            peopleInterested = if (interested) 1 else 0
                        )
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("EventVM", "Failed to load events", e)
                _error.value = "No se pudieron cargar los eventos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(eventId: Int) {
        likedIds = if (eventId in likedIds) likedIds - eventId else likedIds + eventId
        store.saveIds(LocalStateStore.LIKED_EVENTS, likedIds)

        _events.update { current ->
            current.map { event ->
                if (event.id == eventId) {
                    val liked = !event.hasLiked
                    event.copy(hasLiked = liked, likes = if (liked) 1 else 0)
                } else {
                    event
                }
            }
        }
    }

    fun toggleInterest(eventId: Int) {
        interestedIds =
            if (eventId in interestedIds) interestedIds - eventId else interestedIds + eventId
        store.saveIds(LocalStateStore.INTERESTED_EVENTS, interestedIds)

        _events.update { current ->
            current.map { event ->
                if (event.id == eventId) {
                    val interested = !event.isInterested
                    event.copy(
                        isInterested = interested,
                        peopleInterested = if (interested) 1 else 0
                    )
                } else {
                    event
                }
            }
        }
    }
}