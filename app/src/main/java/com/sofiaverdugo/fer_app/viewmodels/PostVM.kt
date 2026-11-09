package com.sofiaverdugo.fer_app.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sofiaverdugo.fer_app.models.Post
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

class PostVM(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.postsApi
    private val store = LocalStateStore(application)
    private var likedIds: Set<Int> = store.getIds(LocalStateStore.LIKED_POSTS)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val likedPosts = posts
        .map { list -> list.filter { it.hasLiked } }
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
                _posts.value = api.getPosts().data.orEmpty()
                    .mapNotNull { it.toModel() }
                    .map { post ->
                        val liked = post.id in likedIds
                        post.copy(hasLiked = liked, likes = if (liked) 1 else 0)
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("PostVM", "Failed to load posts", e)
                _error.value = "No se pudieron cargar las publicaciones"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(postId: Int) {
        likedIds = if (postId in likedIds) likedIds - postId else likedIds + postId
        store.saveIds(LocalStateStore.LIKED_POSTS, likedIds)

        _posts.update { current ->
            current.map { post ->
                if (post.id == postId) {
                    val liked = !post.hasLiked
                    post.copy(hasLiked = liked, likes = if (liked) 1 else 0)
                } else {
                    post
                }
            }
        }
    }
}