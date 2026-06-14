package br.com.oraculo_recomendation.domain.model

enum class MediaType(val label: String) {
    MOVIE("Filme"),
    GAME("Jogo"),
    BOOK("Livro"),
}

data class MediaItem(
    val title: String,
    val type: MediaType,
    val year: Int,
    val creator: String,
    val duration: String,
    val description: String,
    val tags: List<String>,
    val imageUrl: String,
)

data class Recommendation(
    val media: MediaItem,
    val matchScore: Int,
    val reason: String,
)