package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.devmello.gymlog.R
import com.devmello.gymlog.ui.theme.GymLogTheme

@Composable
fun HomeEmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty),
                contentDescription = null,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.large_padding))
                    .size(dimensionResource(id = R.dimen.empty_list_icon_size))
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))
        Text(
            text = stringResource(id = R.string.home_empty_list_message),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyListMessagePreview() {
    GymLogTheme {
        HomeEmptyListMessage()
    }
}