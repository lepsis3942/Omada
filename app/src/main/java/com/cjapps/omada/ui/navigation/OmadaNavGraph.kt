package com.cjapps.omada.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cjapps.omada.ui.HomeScreen

@Composable
fun OmadaNavGraph(
    navHostController: NavHostController = rememberNavController(),
    snackBarHostState: SnackbarHostState,
    modifier: Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = Routes.HOME_SCREEN,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        }) {
        composable(route = Routes.HOME_SCREEN) {
            HomeScreen(modifier, snackBarHostState)
        }
    }
}