package com.sofiaverdugo.fer_app.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.util.Locale

val AppBackground = Color(0xFFF2F2F7)
internal val DetailBackground = Color(0xFF1A3989)
internal val DividerColor = Color(0xFFB5B5B5)
internal val SpanishLocale: Locale = Locale.forLanguageTag("es-MX")

@Composable
internal fun bottomBarSpace(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 100.dp

internal fun UriHandler.openSafely(url: String) {
    if (url.isBlank()) return
    try {
        openUri(url)
    } catch (e: Exception) {
    }
}

@Composable
internal fun CircleIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    containerColor: Color = Color.White,
    tint: Color = MaterialTheme.colorScheme.tertiary,
    borderColor: Color? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(if (borderColor == null) 3.dp else 0.dp, CircleShape)
            .clip(CircleShape)
            .background(containerColor)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, CircleShape) else Modifier
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
internal fun LikeCircleButton(
    liked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    CircleIconButton(
        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = if (liked) "Quitar me gusta" else "Me gusta",
        onClick = onClick,
        modifier = modifier,
        size = size,
        containerColor = if (liked) MaterialTheme.colorScheme.primary else Color.White,
        tint = if (liked) Color.White else MaterialTheme.colorScheme.tertiary
    )
}

@Composable
fun FerTopBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FUNDACIÓN\nESPOSOS\nRODRÍGUEZ",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
internal fun NotFoundMessage(text: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
internal fun DetailLayout(
    imageUrl: String?,
    imageDescription: String?,
    liked: Boolean,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
    onLikeClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val hasImage = !imageUrl.isNullOrBlank()
    val sheetTop = if (hasImage) 360.dp else 130.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
    ) {
        val minSheetHeight = maxHeight - sheetTop

        if (hasImage) {
            AsyncImage(
                model = imageUrl,
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetTop + 40.dp)
                    .graphicsLayer { alpha = 0.5f }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetTop)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircleIconButton(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Regresar",
                        onClick = onBack
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (!hasImage) {
                            LikeCircleButton(liked = liked, onClick = onLikeClick)
                        }
                        CircleIconButton(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            onClick = onShareClick
                        )
                    }
                }

                if (hasImage) {
                    LikeCircleButton(
                        liked = liked,
                        onClick = onLikeClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minSheetHeight)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .navigationBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}