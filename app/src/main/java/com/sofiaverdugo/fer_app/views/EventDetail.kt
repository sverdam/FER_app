package com.sofiaverdugo.fer_app.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sofiaverdugo.fer_app.models.Event
import com.sofiaverdugo.fer_app.viewmodels.EventVM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val eventDayFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", SpanishLocale)

private val shortMonthFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM", SpanishLocale)

private fun formatTime(time: LocalDateTime): String {
    val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12
    val suffix = if (time.hour < 12) "am" else "pm"
    return if (time.minute == 0) {
        "$hour12$suffix"
    } else {
        "$hour12:${time.minute.toString().padStart(2, '0')}$suffix"
    }
}

private fun Event.timeLabel(): String {
    val end = endDateTime
    return if (end != null) "${formatTime(dateTime)} - ${formatTime(end)}" else formatTime(dateTime)
}

@Composable
fun EventDetailScreen(
    eventId: Int,
    onBack: () -> Unit,
    viewModel: EventVM = viewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val events by viewModel.events.collectAsStateWithLifecycle()
    val event = events.firstOrNull { it.id == eventId }

    if (event == null) {
        NotFoundMessage(text = "Evento no encontrado", onBack = onBack)
    } else {
        EventDetailContent(
            event = event,
            onBack = onBack,
            onLikeClick = { viewModel.toggleLike(event.id) },
            onInterestClick = { viewModel.toggleInterest(event.id) },
            onShareClick = {
                Toast.makeText(context, "Compartiendo...", Toast.LENGTH_SHORT).show()
            },
            onCalendarClick = {
                Toast.makeText(context, "Agregando al calendario...", Toast.LENGTH_SHORT).show()
            },
            onRegisterClick = { uriHandler.openSafely(event.registerLink) }
        )
    }
}

@Composable
fun EventDetailContent(
    event: Event,
    onBack: () -> Unit,
    onLikeClick: () -> Unit,
    onInterestClick: () -> Unit,
    onShareClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    DetailLayout(
        imageUrl = event.photo,
        imageDescription = event.title,
        liked = event.hasLiked,
        onBack = onBack,
        onShareClick = onShareClick,
        onLikeClick = onLikeClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = event.address ?: event.location,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            DateItem(event.dateTime)
        }

        HorizontalDivider(color = DividerColor)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Detalles",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailItem(
                        detailTitle = "Fecha",
                        detailBody = event.dateTime.format(eventDayFormatter)
                            .replaceFirstChar { it.uppercase() },
                        icon = Icons.Default.CalendarMonth
                    )
                    DetailItem(
                        detailTitle = "Lugar",
                        detailBody = event.location,
                        icon = Icons.Default.LocationOn
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailItem(
                        detailTitle = "Horario",
                        detailBody = event.timeLabel(),
                        icon = Icons.Default.AccessTimeFilled
                    )
                    DetailItem(
                        detailTitle = "Costo",
                        detailBody = event.cost,
                        icon = Icons.Default.AttachMoney
                    )
                }
            }

            DetailItem(
                detailTitle = "Registro",
                detailBody = event.registerLink.ifBlank { "No requerido" },
                icon = Icons.Default.Link,
                maxLines = 1,
                onClick = if (event.registerLink.isNotBlank()) onRegisterClick else null
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onInterestClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = if (event.isInterested) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                },
                border = if (event.isInterested) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    null
                }
            ) {
                Text(
                    text = if (event.isInterested) "Te interesa" else "Me interesa",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            CircleIconButton(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Agregar al calendario",
                onClick = onCalendarClick,
                size = 56.dp,
                borderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DetailItem(
    detailTitle: String,
    detailBody: String,
    icon: ImageVector,
    maxLines: Int = 2,
    onClick: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column {
            Text(
                text = detailTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = detailBody,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun DateItem(date: LocalDateTime) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            text = date.format(shortMonthFormatter)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailPreview() {
    MaterialTheme {
        EventDetailContent(
            event = Event(
                id = 1,
                title = "Encuentro comunidad FER",
                description = "Los invitamos al primer Encuentro Comunidad FER Talento Sonorense.",
                location = "Teatro COBACH",
                address = "COBACH Villa de Seris, Hermosillo",
                dateTime = LocalDateTime.now().plusDays(7).withHour(16).withMinute(30),
                endDateTime = LocalDateTime.now().plusDays(7).withHour(20).withMinute(0),
                cost = "Gratuito",
                registerLink = "https://luma.com/qe7janb2",
                peopleInterested = 25,
                likes = 8,
                photo = "https://picsum.photos/seed/event1/800/800",
                createdAt = LocalDateTime.now()
            ),
            onBack = {},
            onLikeClick = {},
            onInterestClick = {},
            onShareClick = {},
            onCalendarClick = {},
            onRegisterClick = {}
        )
    }
}