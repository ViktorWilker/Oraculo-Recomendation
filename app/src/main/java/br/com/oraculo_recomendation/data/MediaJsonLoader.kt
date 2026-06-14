package br.com.oraculo_recomendation.data

import android.content.Context
import br.com.oraculo_recomendation.R
import br.com.oraculo_recomendation.data.dto.MediaCatalogDto
import br.com.oraculo_recomendation.domain.model.MediaItem
import br.com.oraculo_recomendation.domain.model.MediaType
import kotlinx.serialization.json.Json

object MediaJsonLoader {

    private val json = Json { ignoreUnknownKeys = true }

    fun load(context: Context): List<MediaItem> {
        val raw = context.resources.openRawResource(R.raw.datas_with_images)
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString<MediaCatalogDto>(raw)
            .media
            .map { dto ->
                MediaItem(
                    title       = dto.title,
                    type        = MediaType.valueOf(dto.type),
                    year        = dto.year,
                    creator     = dto.creator,
                    duration    = dto.duration,
                    description = dto.description,
                    tags        = dto.tags,
                    imageUrl    = dto.imageUrl,
                )
            }
    }
}