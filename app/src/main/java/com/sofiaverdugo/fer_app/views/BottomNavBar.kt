package com.sofiaverdugo.fer_app.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.FEED, "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
    BottomNavItem(Routes.LIKED, "Favoritos", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    BottomNavItem(Routes.EVENTS, "Eventos", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
    BottomNavItem(Routes.INFO, "Información", Icons.Outlined.Info, Icons.Filled.Info),
    BottomNavItem(Routes.SETTINGS, "Ajustes", Icons.Outlined.Settings, Icons.Filled.Settings)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        shape = RoundedCornerShape(50),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                BottomNavBarItem(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else Color(0xFF5F5F5F)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = tint,
            modifier = Modifier.size(30.dp)
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(28.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) tint else Color.Transparent)
        )
    }
}