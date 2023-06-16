package com.example.gymlog.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun AuthenticationScreen(onClickLogin: () -> Unit, onClickRegister: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.welcome_background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    )
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    Pair(0.1f, Color.Transparent),
                    Pair(0.8f, MaterialTheme.colorScheme.background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                .padding(
                    dimensionResource(id = R.dimen.large_padding), vertical = dimensionResource(
                        id = R.dimen.extra_large_padding
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large_padding))
        ) {
            Button(onClick = onClickLogin, modifier = Modifier.fillMaxWidth()) {
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
                onClick = onClickRegister,
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
        AuthenticationScreen(onClickLogin = {}, onClickRegister = {})
    }
}
