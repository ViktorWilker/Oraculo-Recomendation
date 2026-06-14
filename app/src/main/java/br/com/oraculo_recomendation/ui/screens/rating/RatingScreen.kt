package br.com.oraculo_recomendation.ui.screens

import br.com.oraculo_recomendation.domain.model.MediaItem
import br.com.oraculo_recomendation.domain.model.MediaType
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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
fun RatingScreen(
    media: MediaItem,
    currentIndex: Int,
    totalCount: Int,
    onRandomize: (rating: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedRating by remember(media) { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth - 40.dp
    val imageHeight = cardWidth * (3f / 2f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .verticalScroll(scrollState),
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "${currentIndex + 1} / $totalCount", fontSize = 11.sp, color = GoldPrimary)
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(totalCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (index <= currentIndex) GoldPrimary else BorderSubtle),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
            ) {
                AsyncImage(
                    model = media.imageUrl,
                    contentDescription = media.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldFaint)
                        .border(0.5.dp, GoldPrimary.copy(alpha = 0.33f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = media.type.label.uppercase(), fontSize = 10.sp, letterSpacing = 0.12.sp, color = GoldPrimary)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = media.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "${media.year} · ${media.creator} · ${media.duration}", fontSize = 12.sp, color = TextMuted)
                Text(text = media.description, fontSize = 12.sp, color = Color(0xFF666666), lineHeight = 18.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    media.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x661A1500))
                                .border(0.5.dp, Color(0xFF3A2E10), RoundedCornerShape(20.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp),
                        ) {
                            Text(text = tag, fontSize = 10.sp, color = GoldMuted)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = if (selectedRating == 0) "Toque nas estrelas para avaliar" else when (selectedRating) {
                1 -> "Não curtiu"
                2 -> "Mais ou menos"
                3 -> "Legal"
                4 -> "Gostou bastante"
                5 -> "Amou!"
                else -> ""
            },
            fontSize = 12.sp,
            color = if (selectedRating == 0) TextMuted else GoldPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            (1..5).forEach { index ->
                val filled = index <= selectedRating
                val color by animateColorAsState(if (filled) GoldPrimary else BorderSubtle, tween(150), label = "s$index")
                val scale by animateFloatAsState(if (filled) 1.2f else 1f, spring(dampingRatio = 0.4f), label = "sc$index")
                Text(
                    text = "★",
                    fontSize = 36.sp,
                    color = color,
                    modifier = Modifier
                        .scale(scale)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedRating = index },
                )
                if (index < 5) Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        val canRandomize = selectedRating > 0
        val borderColor by animateColorAsState(if (canRandomize) GoldPrimary else BorderSubtle, tween(200), label = "bb")
        val bgColor     by animateColorAsState(if (canRandomize) GoldFaint else Color.Transparent, tween(200), label = "bg")
        val textColor   by animateColorAsState(if (canRandomize) GoldPrimary else TextMuted, tween(200), label = "bt")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(enabled = canRandomize) {
                    val r = selectedRating
                    selectedRating = 0
                    onRandomize(r)
                }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Randomizar ↻", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Medium, letterSpacing = 0.08.sp)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun RatingScreenPreview() {
    RatingScreen(
        media = MediaItem(
            title = "The Fault in Our Stars",
            type = MediaType.BOOK,
            year = 2012,
            creator = "John Green",
            duration = "313 páginas",
            description = "Dois adolescentes com câncer se apaixonam enquanto enfrentam a fragilidade da vida.",
            tags = listOf("Romance", "Drama", "Juvenil"),
            imageUrl = "",
        ),
        currentIndex = 0,
        totalCount = 5,
        onRandomize = {},
    )
}