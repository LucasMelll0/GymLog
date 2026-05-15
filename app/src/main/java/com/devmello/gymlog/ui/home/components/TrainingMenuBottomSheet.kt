package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.devmello.gymlog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingMenuBottomSheet(
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.large_padding))
        ) {
            Text(
                text = stringResource(id = R.string.home_training_menu_bottom_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
            Button(onClick = onClickEdit, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.common_edit))
            }
            OutlinedButton(onClick = onClickDelete, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.common_delete))
            }
        }
    }
}