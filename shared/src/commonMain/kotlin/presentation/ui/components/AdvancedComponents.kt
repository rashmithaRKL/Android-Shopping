package presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object AdvancedComponents {
    @Composable
    fun GradientCard(
        modifier: Modifier = Modifier,
        colors: List<Color> = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        ),
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = modifier
                .drawBehind {
                    val brush = Brush.linearGradient(colors)
                    drawRect(brush)
                },
            shape = RoundedCornerShape(16.dp)
        ) {
            content()
        }
    }

    @Composable
    fun CircularProgressChart(
        progress: Float,
        modifier: Modifier = Modifier,
        strokeWidth: Float = 20f,
        animationDuration: Int = 1000
    ) {
        var animatedProgress by remember { mutableStateOf(0f) }
        
        LaunchedEffect(progress) {
            animate(
                initialValue = animatedProgress,
                targetValue = progress,
                animationSpec = tween(animationDuration)
            ) { value, _ -> animatedProgress = value }
        }

        Canvas(
            modifier = modifier
                .size(200.dp)
                .padding(strokeWidth.dp / 2)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - strokeWidth / 2
            
            // Background circle
            drawCircle(
                color = MaterialTheme.colorScheme.surfaceVariant,
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
            )
            
            // Progress arc
            drawArc(
                color = MaterialTheme.colorScheme.primary,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }

    @Composable
    fun AnimatedWaveBackground(
        modifier: Modifier = Modifier,
        waveColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val wavePhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val path = Path()
            val points = mutableListOf<Offset>()

            for (x in 0..width.toInt() step 10) {
                val y = sin(x * 0.01f + wavePhase) * 50 + height / 2
                points.add(Offset(x.toFloat(), y))
            }

            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }

            drawPath(
                path = path,
                color = waveColor,
                style = Stroke(width = 5f)
            )
        }
    }

    @Composable
    fun CustomRatingBar(
        rating: Float,
        onRatingChanged: (Float) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star $i",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRatingChanged(i.toFloat()) },
                    tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    fun ShimmerLoadingCard(
        modifier: Modifier = Modifier
    ) {
        val transition = rememberInfiniteTransition()
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 200, translateAnim - 200),
            end = Offset(translateAnim, translateAnim)
        )

        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(brush)
            )
        }
    }
}
