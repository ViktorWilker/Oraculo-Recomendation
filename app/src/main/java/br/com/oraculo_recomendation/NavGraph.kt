package br.com.oraculo_recomendation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.oraculo_recomendation.domain.model.MediaType
import br.com.oraculo_recomendation.ui.screens.HomeScreen
import br.com.oraculo_recomendation.ui.screens.RatingScreen
import br.com.oraculo_recomendation.ui.screens.rating.RatingViewModel
import br.com.oraculo_recomendation.ui.screens.recomendations.RecommendationsScreen

private const val ROUTE_HOME            = "home"
private const val ROUTE_RATING          = "rating"
private const val ROUTE_RECOMMENDATIONS = "recommendations"

@Composable
fun NavGraph(modifier: Modifier) {
    val navController = rememberNavController()
    val viewModel: RatingViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = ROUTE_HOME) {

        composable(ROUTE_HOME) {
            HomeScreen(
                onStart = {
                    navController.navigate(ROUTE_RATING)
                },
            )
        }

        composable(ROUTE_RATING) {
            if (uiState.done) {
                navController.navigate(ROUTE_RECOMMENDATIONS) {
                    popUpTo(ROUTE_RATING) { inclusive = true }
                }
            }
            RatingScreen(
                media = uiState.currentMedia,
                currentIndex = uiState.currentIndex,
                totalCount = uiState.totalCount,
                onRandomize = { rating -> viewModel.onRandomize(rating) },
            )
        }

        composable(ROUTE_RECOMMENDATIONS) {
            var activeFilter by remember { mutableStateOf<MediaType?>(null) }
            RecommendationsScreen(
                recommendations = uiState.recommendations,
                activeFilter = activeFilter,
                onFilterChange = { activeFilter = it },
                onItemClick = {},
                onRateAgain = {
                    viewModel.reset()
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_RECOMMENDATIONS) { inclusive = true }
                    }
                },
            )
        }
    }
}