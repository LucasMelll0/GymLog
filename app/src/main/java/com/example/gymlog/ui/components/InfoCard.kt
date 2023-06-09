package com.example.gymlog.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun InfoCard(text: String, modifier: Modifier = Modifier) {

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.default_padding)
            )
        ) {
            Text(
                text = text,
                modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .weight(0.85f)
            )
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.15f)
            )
        }
    }

}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun InfoCardPreview() {
    GymLogTheme {
        InfoCard(text = "orem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus ut ex vel quam porttitor porttitor ac non arcu. Nunc lacinia vestibulum est, eget tempor risus scelerisque vel. Vivamus mollis lobortis quam, nec dapibus risus efficitur eu.")
    }
}