package br.com.oraculo_recomendation.ui.screens.rating

import androidx.lifecycle.ViewModel
import br.com.oraculo_recomendation.data.MockMediaRepository
import br.com.oraculo_recomendation.domain.model.MediaItem
import br.com.oraculo_recomendation.domain.model.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class RatingUiState(
    val currentMedia: MediaItem = MockMediaRepository.getRandomMedia(),
    val currentIndex: Int = 0,
    val totalCount: Int = 5,
    val ratings: Map<MediaItem, Int> = emptyMap(),
    val recommendations: List<Recommendation> = emptyList(),
    val done: Boolean = false,
)

class RatingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState

    fun onRandomize(rating: Int) {
        _uiState.update { state ->
            val newRatings = if (rating > 0)
                state.ratings + (state.currentMedia to rating)
            else
                state.ratings

            val nextIndex = state.currentIndex + 1

            if (nextIndex >= state.totalCount) {
                val recs = MockMediaRepository.getRecommendations(newRatings)
                state.copy(ratings = newRatings, recommendations = recs, done = true)
            } else {
                state.copy(
                    currentMedia = MockMediaRepository.getRandomMedia(),
                    currentIndex = nextIndex,
                    ratings = newRatings,
                )
            }
        }
    }

    fun reset() {
        _uiState.value = RatingUiState(currentMedia = MockMediaRepository.getRandomMedia())
    }
}