package com.example.gymlog.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun CustomLinearProgressBar(modifier: Modifier = Modifier, percent: Float, text: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(modifier = modifier
        .clip(MaterialTheme.shapes.large)
        .background(MaterialTheme.colorScheme.primary.copy(0.1f))
        .height(50.dp)
        .fillMaxWidth()
        .drawWithContent {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // Destination
                drawContent()

                // Source
                drawRect(
                    color = primaryColor, size = Size(size.width * percent, size.height),
                    blendMode = BlendMode.SrcOut
                )
                restoreToCount(checkPoint)
            }
        }) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
private fun CustomProgressBarPreview() {
    GymLogTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            CustomLinearProgressBar(
                percent = 0.5f,
                text = "50% Completo",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun CustomCircularProgressbar(progress: Float, text: String, modifier: Modifier = Modifier) {
    val size = 180.dp
    val indicatorThickness = 20.dp
    val progressBackgroundColor = MaterialTheme.colorScheme.primary.copy(0.1f)
    val progressColor = MaterialTheme.colorScheme.primary
    val animateNumber by animateFloatAsState(targetValue = progress, animationSpec = tween())
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = progressBackgroundColor,
                radius = size.toPx() / 2,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
            val sweepAngle = (animateNumber / 100) * 360
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(text = text, style = MaterialTheme.typography.titleLarge)

    }
}

@Preview
@Composable
private fun CustomCircularProgressbarPreview() {
    GymLogTheme {
        Card() {
            CustomCircularProgressbar(
                50f,
                "00m 10s",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}