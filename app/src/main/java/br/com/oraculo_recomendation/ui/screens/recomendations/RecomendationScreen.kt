package br.com.oraculo_recomendation.ui.screens.recomendations

import br.com.oraculo_recomendation.domain.model.MediaItem
import br.com.oraculo_recomendation.domain.model.MediaType
import br.com.oraculo_recomendation.domain.model.Recommendation
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

private val Background   = Color(0xFF0A0A0A)
private val Surface      = Color(0xFF111111)
private val GoldPrimary  = Color(0xFFC9A84C)
private val GoldMuted    = Color(0xFF4A3E1A)
private val GoldFaint    = Color(0x1AC9A84C)
private val BorderSubtle = Color(0xFF2A2A2A)
private val TextPrimary  = Color(0xFFE8D9B0)
private val TextMuted    = Color(0xFF4A4A4A)
private val TextHint     = Color(0xFF2E2E2E)

@Composable
fun RecommendationsScreen(
    recommendations: List<Recommendation>,
    activeFilter: MediaType?,
    onFilterChange: (MediaType?) -> Unit,
    onItemClick: (MediaItem) -> Unit,
    onRateAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filtered = if (activeFilter == null) recommendations
    else recommendations.filter { it.media.type == activeFilter }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        item { RecommendationsHeader() }

        item {
            FilterChips(active = activeFilter, onChange = onFilterChange, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(24.dp))
        }

        itemsIndexed(filtered) { index, rec ->
            RecommendationCard(
                rec = rec,
                index = index,
                onClick = { onItemClick(rec.media) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            if (index < filtered.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = BorderSubtle)
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            RateAgainButton(onClick = onRateAgain)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "O oráculo aprende a cada avaliação", fontSize = 11.sp, color = TextHint, letterSpacing = 0.05.sp)
            }
        }
    }
}

@Composable
private fun RecommendationsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F0D07), Background))),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(GoldPrimary.copy(alpha = 0.2f)))
        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Suas Recomendações", fontSize = 26.sp, fontWeight = FontWeight.Light, color = TextPrimary, letterSpacing = (-0.3).sp)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(Brush.horizontalGradient(colors = listOf(Color.Transparent, GoldPrimary, Color.Transparent))),
            )
        }
    }
}

@Composable
private fun FilterChips(active: MediaType?, onChange: (MediaType?) -> Unit, modifier: Modifier = Modifier) {
    val options: List<Pair<String, MediaType?>> = listOf(
        "Todos" to null,
        "Filmes" to MediaType.MOVIE,
        "Jogos"  to MediaType.GAME,
        "Livros" to MediaType.BOOK,
    )
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(options) { _, (label, type) ->
            val selected = active == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) GoldFaint else Color.Transparent)
                    .border(0.5.dp, if (selected) GoldPrimary.copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(20.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onChange(type) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (selected) GoldPrimary else TextMuted,
                    letterSpacing = 0.05.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(rec: Recommendation, index: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(rec.media.title) {
        delay(index * 60L)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "card_alpha_$index",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        PosterThumbnail(imageUrl = rec.media.imageUrl, matchScore = rec.matchScore)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "${rec.media.type.label.uppercase()} · ${rec.media.year}", fontSize = 10.sp, color = TextMuted, letterSpacing = 0.1.sp)
            Text(text = rec.media.title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${rec.media.creator} · ${rec.media.duration}", fontSize = 12.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(text = rec.reason, fontSize = 11.sp, color = GoldMuted, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rec.media.tags.take(2).forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1A1500))
                            .border(0.5.dp, Color(0xFF3A2E10), RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(text = tag, fontSize = 10.sp, color = GoldMuted)
                    }
                }
            }
        }
        Text(text = "›", fontSize = 18.sp, color = BorderSubtle, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun PosterThumbnail(imageUrl: String, matchScore: Int) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 96.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xCC000000)
                        )
                    )
                )
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$matchScore%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = GoldPrimary
            )
        }
    }
}

@Composable
private fun RateAgainButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GoldFaint)
            .border(0.5.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .drawBehind {
                drawLine(color = GoldPrimary.copy(alpha = 0.15f), start = Offset(size.width * 0.3f, 0f), end = Offset(size.width * 0.7f, 0f), strokeWidth = 1f)
            }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Refinar recomendações ↻", fontSize = 13.sp, color = GoldPrimary, fontWeight = FontWeight.Medium, letterSpacing = 0.08.sp)
    }
}
