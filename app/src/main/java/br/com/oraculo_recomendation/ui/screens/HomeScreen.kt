package br.com.oraculo_recomendation.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.oraculo_recomendation.R
import kotlinx.coroutines.delay

private val Background   = Color(0xFF000000)
private val GoldPrimary  = Color(0xFFC9A84C)
private val GoldFaint    = Color(0x1AC9A84C)
private val BorderSubtle = Color(0xFF2A2A2A)
private val TextPrimary  = Color(0xFFE8D9B0)
private val TextMuted    = Color(0xFF555555)
private val CardBg       = Color(0xFF0F0F0F)

@Composable
fun HomeScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "home_alpha",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .alpha(alpha),
    ) {
        Image(
            painter = painterResource(id = R.drawable.oraculo_logo),
            contentDescription = "Oráculo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.5f to Color.Transparent,
                            1.0f to Background,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardBg)
                .border(
                    width = 0.5.dp,
                    color = BorderSubtle,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                )
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Descubra o que consumir\npara não ser consumido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
            )
            Text(
                text = "Avalie algumas mídias e o oráculo revela recomendações feitas para o seu gosto.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldFaint)
                    .border(0.5.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStart,
                    )
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Consultar o Oráculo",
                    fontSize = 15.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.1.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(onStart = {})
}