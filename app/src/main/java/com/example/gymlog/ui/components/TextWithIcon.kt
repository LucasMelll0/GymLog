package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun TextWithIcon(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
    ) {
        icon()
        Text(text = text, style = style)
    }
}

@Preview
@Composable
private fun TextWithIconPreview() {
    GymLogTheme {
        TextWithIcon(text = "Teste", icon = {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null
            )
        })
    }
}