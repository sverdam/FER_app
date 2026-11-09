package com.sofiaverdugo.fer_app.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sofiaverdugo.fer_app.models.UserProfile
import com.sofiaverdugo.fer_app.viewmodels.EventVM
import com.sofiaverdugo.fer_app.viewmodels.PostVM

object Routes {
    const val FEED = "feed"
    const val LIKED = "liked"
    const val EVENTS = "events"
    const val INFO = "info"
    const val SETTINGS = "settings"

    const val POST_ID = "postId"
    const val EVENT_ID = "eventId"
    const val POST = "post/{$POST_ID}"
    const val EVENT = "event/{$EVENT_ID}"

    fun post(id: Int) = "post/$id"
    fun event(id: Int) = "event/$id"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    postViewModel: PostVM = viewModel(),
    eventViewModel: EventVM = viewModel(),
    profile: UserProfile = UserProfile()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.FEED
    ) {
        composable(Routes.FEED) {
            MainFeed(
                modifier = modifier,
                postViewModel = postViewModel,
                eventViewModel = eventViewModel,
                onPostClick = { id -> navController.navigate(Routes.post(id)) },
                onEventClick = { id -> navController.navigate(Routes.event(id)) },
                profile = profile
            )
        }

        composable(Routes.LIKED) {
            LikesScreen(
                modifier = modifier,
                postViewModel = postViewModel,
                eventViewModel = eventViewModel,
                onPostClick = { id -> navController.navigate(Routes.post(id)) },
                onEventClick = { id -> navController.navigate(Routes.event(id)) }
            )
        }

        composable(Routes.EVENTS) { PlaceholderScreen("Eventos", modifier) }
        composable(Routes.INFO) { PlaceholderScreen("Información", modifier) }
        composable(Routes.SETTINGS) { PlaceholderScreen("Ajustes", modifier) }

        composable(
            route = Routes.POST,
            arguments = listOf(navArgument(Routes.POST_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt(Routes.POST_ID) ?: return@composable
            PostDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() },
                viewModel = postViewModel
            )
        }

        composable(
            route = Routes.EVENT,
            arguments = listOf(navArgument(Routes.EVENT_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt(Routes.EVENT_ID) ?: return@composable
            EventDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                viewModel = eventViewModel
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
    }
}