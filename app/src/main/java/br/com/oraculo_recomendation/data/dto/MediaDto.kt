package br.com.oraculo_recomendation.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val title: String,
    val type: String,
    val year: Int,
    val creator: String,
    val duration: String,
    val description: String,
    val tags: List<String>,
    val imageUrl: String
)

@Serializable
data class MediaCatalogDto(
    val media: List<MediaDto>
)