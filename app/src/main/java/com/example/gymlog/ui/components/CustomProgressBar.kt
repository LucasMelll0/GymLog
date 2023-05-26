package com.example.gymlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun CustomProgressBar(modifier: Modifier = Modifier, percent: Float, text: String) {
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
            CustomProgressBar(
                percent = 0.5f,
                text = "50% Completo",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}