package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.devmello.gymlog.R

@Composable
fun TrainingItemShimmer(
    modifier: Modifier = Modifier,
    shimmerModifier: Modifier
) {
    Card(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.large_padding))
                .fillMaxWidth()
                .heightIn(
                    min = dimensionResource(
                        id = R.dimen.minimum_training_item_height
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .then(shimmerModifier)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .then(shimmerModifier)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .then(shimmerModifier)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.default_padding))
                    .heightIn(min = 40.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(32.dp)
                            .clip(CircleShape)
                            .then(shimmerModifier)
                    )
                }
            }
        }
    }
}