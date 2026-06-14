package br.com.oraculo_recomendation.data

import android.content.Context
import br.com.oraculo_recomendation.domain.model.MediaItem
import br.com.oraculo_recomendation.domain.model.MediaType
import br.com.oraculo_recomendation.domain.model.Recommendation
import kotlin.math.sqrt

object MockMediaRepository {

    private lateinit var allMedia: List<MediaItem>
    private val shown = mutableSetOf<String>()

    fun initialize(context: Context) {
        allMedia = MediaJsonLoader.load(context)
    }

    fun getRandomMedia(): MediaItem {
        check(::allMedia.isInitialized) { "Call initialize() first" }
        val available = allMedia.filter { it.title !in shown }
        if (available.isEmpty()) shown.clear()
        val next = (if (available.isEmpty()) allMedia else available).random()
        shown.add(next.title)
        return next
    }

    fun getRecommendations(ratings: Map<MediaItem, Int>): List<Recommendation> {
        check(::allMedia.isInitialized) { "Call initialize() first" }
        if (ratings.isEmpty()) return emptyList()

        val ratedTitles = ratings.keys.map { it.title }.toSet()
        val candidates  = allMedia.filter { it.title !in ratedTitles }
        val userVector  = buildUserVector(ratings)
        val vectorNorm  = norm(userVector)

        val scored = candidates
            .map { item ->
                val itemVector = buildItemVector(item, userVector.keys)
                val raw        = cosineSimilarity(userVector, itemVector, vectorNorm)
                Recommendation(item, normalizeScore(raw), buildReason(item, ratings))
            }
            .sortedByDescending { it.matchScore }

        val result      = mutableListOf<Recommendation>()
        val usedTitles  = mutableSetOf<String>()
        val MIN_PER_TYPE = 3

        for (type in MediaType.entries) {
            scored
                .filter { it.media.type == type && it.media.title !in usedTitles }
                .take(MIN_PER_TYPE)
                .forEach {
                    result.add(it)
                    usedTitles.add(it.media.title)
                }
        }

        scored
            .filter { it.media.title !in usedTitles }
            .take(12 - result.size)
            .forEach {
                result.add(it)
                usedTitles.add(it.media.title)
            }

        return result.sortedByDescending { it.matchScore }
    }

    private fun buildUserVector(ratings: Map<MediaItem, Int>): Map<String, Double> {
        val vector = mutableMapOf<String, Double>()
        val typeGroups = ratings.entries.groupBy { it.key.type }
        typeGroups.forEach { (type, entries) ->
            val avg = entries.map { it.value }.average()
            val weight = ((avg - 3.0) / 2.0) * 1.5
            vector["type:${type.name}"] = weight
        }

        ratings.entries.forEach { (item, rating) ->
            val weight = (rating - 3.0) / 2.0
            item.tags.forEach { tag ->
                vector["tag:$tag"] = (vector["tag:$tag"] ?: 0.0) + weight
            }
        }

        return vector
    }


    private fun buildItemVector(item: MediaItem, keys: Set<String>): Map<String, Double> {
        val vector = mutableMapOf<String, Double>()
        val typeKey = "type:${item.type.name}"
        if (typeKey in keys) vector[typeKey] = 1.5
        item.tags.forEach { tag ->
            val tagKey = "tag:$tag"
            if (tagKey in keys) vector[tagKey] = 1.0
        }
        return vector
    }

    private fun normalizeScore(cosine: Double): Int {
        val clamped = cosine.coerceIn(-1.0, 1.0)
        return (((clamped + 1.0) / 2.0) * 68 + 30).toInt().coerceIn(30, 98)
    }

    private fun cosineSimilarity(
        a: Map<String, Double>,
        b: Map<String, Double>,
        normA: Double
    ): Double {
        if (normA == 0.0) return 0.0
        val dot = a.entries.sumOf { (k, v) -> v * (b[k] ?: 0.0) }
        val normB = norm(b)
        return if (normB == 0.0) 0.0 else dot / (normA * normB)
    }

    private fun norm(v: Map<String, Double>) = sqrt(v.values.sumOf { it * it })

    private fun buildReason(item: MediaItem, ratings: Map<MediaItem, Int>): String {
        val highRated = ratings.filter { it.value >= 4 }.keys
        val lowRated = ratings.filter { it.value <= 2 }.keys
        val sameType = highRated.filter { it.type == item.type }
        val sharedTags =
            highRated.filter { rated -> rated.tags.intersect(item.tags.toSet()).isNotEmpty() }
        val avoidTags = lowRated.flatMap { it.tags }.toSet()
        val uniqueTags = item.tags.filter { it !in avoidTags }

        return when {
            sameType.isNotEmpty() && sharedTags.isNotEmpty() ->
                "Estilo e gênero similares a ${sameType.first().title}"

            sameType.isNotEmpty() ->
                "Baseado em ${sameType.first().title}, que você avaliou bem"

            sharedTags.isNotEmpty() ->
                "Compartilha elementos de ${sharedTags.first().title}"

            uniqueTags.isNotEmpty() ->
                "Explora ${uniqueTags.first()}, fora do que você já viu"

            else ->
                "Expande seu gosto com algo diferente"
        }
    }
}