package com.example.gymlog.ui.bmi

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymlog.R
import com.example.gymlog.data.ages
import com.example.gymlog.data.heights
import com.example.gymlog.model.User
import com.example.gymlog.ui.components.InfoCard
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.Gender
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UserCreatorDialog(
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit,
    userToupdate: User? = null
) {
    val scope = rememberCoroutineScope()
    val heightsListState = rememberLazyListState()
    val agesListState = rememberLazyListState()
    var selectedGender: Gender by rememberSaveable {
        val initialValue = userToupdate?.gender ?: Gender.Male
        mutableStateOf(initialValue)
    }
    var selectedHeight: Int by rememberSaveable {
        val initialValue = userToupdate?.height ?: heights[0]
        scope.launch {
            heightsListState.scrollToItem(heights.indexOf(initialValue))
        }
        mutableStateOf(initialValue)
    }
    var selectedAge: Int by rememberSaveable {
        val initialValue = userToupdate?.age ?: ages[0]
        scope.launch {
            agesListState.scrollToItem(ages.indexOf(initialValue))
        }
        mutableStateOf(initialValue)
    }
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoCard(
                    text = stringResource(id = R.string.user_creator_info_card_text),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                )
                Text(
                    text = stringResource(id = R.string.bmi_calculator_gender_label),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                )
                GenderSelector(
                    onSelectedListener = { selectedGender = it },
                    selected = selectedGender,
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
                )
                Divider()
                Text(
                    text = stringResource(id = R.string.bmi_calculator_height_label),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                )
                HeightSelector(
                    heights = heights,
                    selected = selectedHeight,
                    onItemClickListener = { selectedHeight = it },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                    listState = heightsListState
                )
                Text(
                    text = stringResource(id = R.string.bmi_calculator_age_label),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                )
                AgeSelector(
                    ages = ages,
                    selected = selectedAge,
                    onItemClickListener = { selectedAge = it },
                    listState = agesListState
                )
                Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.default_padding)
                    ),
                    modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.default_padding)
                    )
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(0.4f)) {
                        Text(text = stringResource(id = R.string.common_cancel))
                    }
                    Button(onClick = {
                        val user = User(
                            id = userToupdate?.id ?: UUID.randomUUID().toString(),
                            gender = selectedGender,
                            height = selectedHeight,
                            age = selectedAge
                        )
                        onConfirm(user)
                    }, modifier = Modifier.weight(0.6f)) {
                        Text(text = stringResource(id = R.string.common_save))
                    }
                }
            }
        }
    }
}

@Composable
fun HeightSelector(
    heights: List<Int>,
    selected: Int,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClickListener: (selected: Int) -> Unit
) {

    LazyRow(modifier = modifier, state = listState) {
        items(heights, key = { it }) { item ->
            val isSelected = item == selected
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .animateContentSize()
                        .height(dimensionResource(id = R.dimen.height_selector_height))
                        .clickable { onItemClickListener(item) }) {
                    AnimatedVisibility(
                        visible = isSelected
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_drop_down),
                            contentDescription = null
                        )

                    }
                    Text(
                        text = "${item}cm",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.default_padding)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelector(
    onSelectedListener: (Gender) -> Unit, selected: Gender, modifier: Modifier = Modifier
) {
    val genders = Gender.values()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(genders) {
            val isSelected = it == selected
            InputChip(selected = isSelected, onClick = { onSelectedListener(it) }, label = {
                Text(
                    text = stringResource(
                        id = it.stringRes()
                    ), style = MaterialTheme.typography.titleLarge
                )
            }, colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        }
    }
}

@Composable
fun AgeSelector(
    ages: List<Int>,
    selected: Int,
    onItemClickListener: (selected: Int) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()

) {
    LazyRow(modifier = modifier, state = listState) {
        items(ages, key = { it }) { item ->
            val isSelected = item == selected
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .animateContentSize()
                        .height(dimensionResource(id = R.dimen.height_selector_height))
                        .clickable { onItemClickListener(item) }) {
                    AnimatedVisibility(
                        visible = isSelected
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_drop_down),
                            contentDescription = null
                        )

                    }
                    Text(
                        text = stringResource(id = R.string.common_age_suffix, item),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.default_padding)
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AgeSelectorPreview() {
    GymLogTheme {
        val ages = mutableListOf<Int>().apply {
            for (i in 10..110) {
                add(i)
            }
        }
        var selected by remember { mutableStateOf(ages[0]) }
        AgeSelector(ages = ages, onItemClickListener = { selected = it }, selected = selected)
    }
}

@Preview
@Composable
private fun GenderSelectorPreview() {
    GymLogTheme {
        Surface {
            var selected: Gender by remember {
                mutableStateOf(Gender.Male)
            }
            GenderSelector(
                onSelectedListener = { selected = it },
                selected = selected,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeightSelectorPreview() {
    GymLogTheme {
        Surface {

            val heights = mutableListOf<Int>().apply {
                for (i in 120..250) {
                    add(i)
                }
            }
            var selected: Int by remember {
                mutableStateOf(heights[0])
            }
            HeightSelector(
                heights = heights,
                onItemClickListener = { selected = it },
                selected = selected,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun UserCreatorDialogPreview() {
    GymLogTheme {
        UserCreatorDialog(onDismiss = {}, onConfirm = {})
    }
}