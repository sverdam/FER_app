package com.sofiaverdugo.fer_app.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sofiaverdugo.fer_app.models.Event
import com.sofiaverdugo.fer_app.models.Post
import com.sofiaverdugo.fer_app.models.UserProfile
import com.sofiaverdugo.fer_app.models.matchesProfile
import com.sofiaverdugo.fer_app.viewmodels.EventVM
import com.sofiaverdugo.fer_app.viewmodels.PostVM
import java.time.LocalDateTime

internal sealed class FeedItem {
    abstract val id: String
    abstract val dateTime: LocalDateTime
    abstract val searchText: String
    abstract val audiences: Set<String>
    abstract val levels: Set<String>

    data class PostItem(val post: Post) : FeedItem() {
        override val id: String = "post_${post.id}"
        override val dateTime: LocalDateTime = post.createdAt
        override val searchText: String =
            listOfNotNull(post.title, post.description, post.body).joinToString(" ")
        override val audiences: Set<String> = post.audiences
        override val levels: Set<String> = post.levels
    }

    data class EventItem(val event: Event) : FeedItem() {
        override val id: String = "event_${event.id}"
        override val dateTime: LocalDateTime = event.createdAt
        override val searchText: String =
            listOfNotNull(event.title, event.description, event.location, event.address)
                .joinToString(" ")
        override val audiences: Set<String> = event.audiences
        override val levels: Set<String> = event.levels
    }
}

internal fun buildFeedItems(posts: List<Post>, events: List<Event>): List<FeedItem> =
    buildList<FeedItem> {
        addAll(posts.map { FeedItem.PostItem(it) })
        addAll(events.map { FeedItem.EventItem(it) })
    }.sortedByDescending { it.dateTime }

internal fun FeedItem.matches(query: String): Boolean {
    val q = query.trim()
    return q.isEmpty() || searchText.contains(q, ignoreCase = true)
}

internal fun FeedItem.visibleTo(profile: UserProfile): Boolean =
    matchesProfile(audiences, levels, profile)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeed(
    modifier: Modifier = Modifier,
    postViewModel: PostVM = viewModel(),
    eventViewModel: EventVM = viewModel(),
    onPostClick: (Int) -> Unit = {},
    onEventClick: (Int) -> Unit = {},
    profile: UserProfile = UserProfile()
) {
    val posts by postViewModel.posts.collectAsStateWithLifecycle()
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val postsLoading by postViewModel.isLoading.collectAsStateWithLifecycle()
    val eventsLoading by eventViewModel.isLoading.collectAsStateWithLifecycle()
    val postsError by postViewModel.error.collectAsStateWithLifecycle()
    val eventsError by eventViewModel.error.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    val items = remember(posts, events, query, profile) {
        buildFeedItems(posts, events)
            .filter { it.visibleTo(profile) && it.matches(query) }
    }
    val error = postsError ?: eventsError

    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            query = query,
            onQueryChange = { query = it }
        )

        if (error != null && items.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }

        PullToRefreshBox(
            isRefreshing = postsLoading || eventsLoading,
            onRefresh = {
                postViewModel.refresh()
                eventViewModel.refresh()
            },
            modifier = Modifier.weight(1f)
        ) {
            FeedList(
                items = items,
                onPostClick = onPostClick,
                onEventClick = onEventClick,
                onPostLike = postViewModel::toggleLike,
                onEventLike = eventViewModel::toggleLike,
                emptyMessage = when {
                    error != null && items.isEmpty() ->
                        "$error\nDesliza hacia abajo para reintentar"
                    query.isBlank() -> "Aún no hay publicaciones"
                    else -> "Sin resultados para \"$query\""
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun LikesScreen(
    modifier: Modifier = Modifier,
    postViewModel: PostVM = viewModel(),
    eventViewModel: EventVM = viewModel(),
    onPostClick: (Int) -> Unit = {},
    onEventClick: (Int) -> Unit = {}
) {
    val likedPosts by postViewModel.likedPosts.collectAsStateWithLifecycle()
    val likedEvents by eventViewModel.likedEvents.collectAsStateWithLifecycle()

    val items = remember(likedPosts, likedEvents) {
        buildFeedItems(likedPosts, likedEvents)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Mis likes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        FeedList(
            items = items,
            onPostClick = onPostClick,
            onEventClick = onEventClick,
            onPostLike = postViewModel::toggleLike,
            onEventLike = eventViewModel::toggleLike,
            emptyMessage = "Aún no has dado like a nada",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(4.dp, RoundedCornerShape(50)),
        singleLine = true,
        shape = RoundedCornerShape(50),
        placeholder = { Text("Buscar en publicaciones...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Borrar búsqueda"
                    )
                }
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
internal fun FeedList(
    items: List<FeedItem>,
    onPostClick: (Int) -> Unit,
    onEventClick: (Int) -> Unit,
    onPostLike: (Int) -> Unit,
    onEventLike: (Int) -> Unit,
    emptyMessage: String,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = bottomBarSpace()
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                when (item) {
                    is FeedItem.PostItem -> PostCard(
                        post = item.post,
                        onClick = { onPostClick(item.post.id) },
                        onLikeClick = { onPostLike(item.post.id) }
                    )

                    is FeedItem.EventItem -> EventCard(
                        event = item.event,
                        onClick = { onEventClick(item.event.id) },
                        onLikeClick = { onEventLike(item.event.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FeedCard(
        imageUrl = post.thumbnail,
        title = post.title,
        description = post.description ?: post.body,
        liked = post.hasLiked,
        onClick = onClick,
        onLikeClick = onLikeClick,
        modifier = modifier
    )
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FeedCard(
        imageUrl = event.photo,
        title = event.title,
        description = event.description,
        liked = event.hasLiked,
        onClick = onClick,
        onLikeClick = onLikeClick,
        modifier = modifier,
        badge = "Evento"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedCard(
    imageUrl: String?,
    title: String?,
    description: String?,
    liked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    val hasImage = !imageUrl.isNullOrBlank()

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            if (hasImage) {
                Box {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.6f)
                            .background(Color(0xFFE3E3EA))
                    )

                    if (badge != null) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    LikeCircleButton(
                        liked = liked,
                        onClick = onLikeClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!title.isNullOrBlank()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (!description.isNullOrBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!hasImage) {
                    LikeCircleButton(
                        liked = liked,
                        onClick = onLikeClick,
                        size = 40.dp
                    )
                }
            }
        }
    }
}