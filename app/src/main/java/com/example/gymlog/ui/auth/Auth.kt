package com.example.gymlog.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun AuthenticationScreen() {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.large_padding)))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(dimensionResource(id = R.dimen.large_padding))
        ) {
            Text(
                text = stringResource(id = R.string.common_welcome),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.auth_welcome_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.2f)
                .padding(dimensionResource(id = R.dimen.large_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large_padding))
        ) {
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.common_login),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(
                            id = R.dimen.default_padding
                        )
                    )
                )
            }
            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.common_register),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(
                            id = R.dimen.default_padding
                        )
                    )
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(showSystemUi = true)
@Composable
fun AuthenticationScreenPreview() {
    GymLogTheme {
        AuthenticationScreen()
    }
}
