package com.sofiaverdugo.fer_app.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sofiaverdugo.fer_app.models.Post
import com.sofiaverdugo.fer_app.models.PostAttachment
import com.sofiaverdugo.fer_app.viewmodels.PostVM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val postDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd 'de' MMMM", SpanishLocale)

@Composable
fun PostDetailScreen(
    postId: Int,
    onBack: () -> Unit,
    viewModel: PostVM = viewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val post = posts.firstOrNull { it.id == postId }

    if (post == null) {
        NotFoundMessage(text = "Publicación no encontrada", onBack = onBack)
    } else {
        PostDetailContent(
            post = post,
            onBack = onBack,
            onLikeClick = { viewModel.toggleLike(post.id) },
            onShareClick = {
                Toast.makeText(context, "Compartiendo...", Toast.LENGTH_SHORT).show()
            },
            onAttachmentClick = { url -> uriHandler.openSafely(url) }
        )
    }
}

@Composable
fun PostDetailContent(
    post: Post,
    onBack: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onAttachmentClick: (String) -> Unit
) {
    val dateText = post.createdAt.format(postDateFormatter).let {
        if (post.updatedAt != null) "$it · editado" else it
    }

    DetailLayout(
        imageUrl = post.thumbnail,
        imageDescription = post.title,
        liked = post.hasLiked,
        onBack = onBack,
        onShareClick = onShareClick,
        onLikeClick = onLikeClick
    ) {
        Column {
            post.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        HorizontalDivider(color = DividerColor)

        post.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        if (post.body.isNotBlank()) {
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        post.attachments.forEach { attachment ->
            PostAttachmentItem(
                attachment = attachment,
                onClick = onAttachmentClick
            )
        }
    }
}

@Composable
internal fun PostAttachmentItem(
    attachment: PostAttachment,
    onClick: (String) -> Unit
) {
    val mediaShape = RoundedCornerShape(24.dp)

    when (attachment) {
        is PostAttachment.Image -> {
            val ratio = if (attachment.width != null && attachment.height != null && attachment.height > 0) {
                attachment.width / attachment.height.toFloat()
            } else {
                null
            }

            AsyncImage(
                model = attachment.url,
                contentDescription = "Imagen adjunta",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(mediaShape)
                    .then(
                        if (ratio != null) Modifier.aspectRatio(ratio) else Modifier.height(240.dp)
                    )
            )
        }

        is PostAttachment.Video -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(mediaShape)
                    .background(Color(0xFF1A1A1A))
                    .clickable { onClick(attachment.url) }
            ) {
                attachment.thumbnailUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Miniatura del video",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Reproducir video",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        is PostAttachment.Link -> {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(attachment.url) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = attachment.title ?: "Enlace",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = attachment.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        attachment.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        is PostAttachment.File -> {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(attachment.url) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = attachment.name,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = attachment.mimeType,
                            style = MaterialTheme.typography.bodySmall
                        )
                        attachment.size?.let {
                            Text(
                                text = "${it / 1024} KB",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailPreview() {
    MaterialTheme {
        PostDetailContent(
            post = Post(
                id = 1,
                title = "80 años creyendo en ti 💙",
                body = "Creer en un joven es invertir en el futuro de Sonora.",
                thumbnail = "https://picsum.photos/seed/post1/800/800",
                attachments = listOf(
                    PostAttachment.Video(
                        id = "v1",
                        url = "https://example.com/video",
                        thumbnailUrl = "https://picsum.photos/seed/video1/600/800"
                    )
                ),
                authorId = 1,
                createdAt = LocalDateTime.now().minusDays(1)
            ),
            onBack = {},
            onLikeClick = {},
            onShareClick = {},
            onAttachmentClick = {}
        )
    }
}