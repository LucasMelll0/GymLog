package com.example.gymlog.ui.bmi

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.database.AppDataBase_Impl
import com.example.gymlog.extensions.isZeroOrEmpty
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.repository.BmiInfoRepositoryImpl
import com.example.gymlog.ui.bmi.viewmodel.BmiCalculatorViewModel
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.components.TextWithIcon
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.ui.theme.md_theme_dark_onPrimary
import com.example.gymlog.ui.theme.warning_color
import com.example.gymlog.utils.BmiClassifier
import com.example.gymlog.utils.BmiRating
import com.example.gymlog.utils.Gender
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun BmiCalculatorScreen(
    onNavIconClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: BmiCalculatorViewModel = koinViewModel()
) {
    val heights = mutableListOf<Int>().apply {
        for (i in 120..250) {
            add(i)
        }
    }
    var selectedHeight: Int by rememberSaveable { mutableStateOf(heights[0]) }
    var selectedGender: Gender by rememberSaveable { mutableStateOf(Gender.Male) }
    var weight by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var weightHasError by remember { mutableStateOf(false) }
    var ageHasError by remember { mutableStateOf(false) }
    var classifier: BmiClassifier? by remember { mutableStateOf(null) }
    var isLoading: Boolean by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold(bottomBar = { BmiCalculatorBottomBar(onNavIconClick = onNavIconClick) }) { paddingValues ->
        Box {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(2f))
            }
            Column(
                modifier = Modifier
                    .zIndex(1f)
                    .padding(paddingValues)
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.bmi_calculator_title),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Card(modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))) {
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
                    }
                    Card(modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))) {
                        Text(
                            text = stringResource(id = R.string.bmi_calculator_height_label),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                        )
                        HeightSelector(
                            heights = heights,
                            selected = selectedHeight,
                            onItemClickListener = { selectedHeight = it },
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.default_padding)
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
                    ) {
                        DefaultTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text(text = stringResource(id = R.string.bmi_calculator_weight_label)) },
                            modifier = Modifier.weight(0.5f),
                            charLimit = 3,
                            suffix = { Text(text = stringResource(id = R.string.common_kg_suffix)) },
                            isError = weightHasError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        DefaultTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text(text = stringResource(id = R.string.bmi_calculator_age_label)) },
                            modifier = Modifier.weight(0.5f),
                            charLimit = 3,
                            isError = ageHasError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            suffix = { Text(text = stringResource(id = R.string.common_years)) }
                        )
                    }
                }
                AnimatedVisibility(visible = classifier != null) {
                    classifier?.let {
                        ResultCard(
                            classifier = it, modifier = Modifier.padding(
                                dimensionResource(
                                    id = R.dimen.default_padding
                                )
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.default_padding)),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.default_padding)
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            classifier?.let {
                                scope.launch {
                                    isLoading = true
                                    val bmiInfo = BmiInfo(
                                        gender = it.gender,
                                        weight = it.weight,
                                        height = it.height,
                                        age = it.age
                                    )
                                    try {
                                        viewModel.save(bmiInfo)
                                        isLoading = false
                                        onSaved()
                                    } catch (e: Exception) {
                                        isLoading = false
                                        Log.w("BmiCalculator", "BmiCalculatorScreen: ", e)
                                    }

                                }
                            } ?: run {
                                isLoading = true
                                weightHasError = weight.isZeroOrEmpty()
                                ageHasError = age.isZeroOrEmpty()
                                if (!weightHasError && !ageHasError) {
                                    if (weight.isDigitsOnly() && age.isDigitsOnly()) {
                                        scope.launch {
                                            classifier = BmiClassifier(
                                                gender = selectedGender,
                                                weight = weight.toFloat(),
                                                height = selectedHeight,
                                                age = age.toInt()
                                            )
                                        }
                                    }
                                }
                                isLoading = false
                            }
                        }, modifier = Modifier
                            .weight(0.7f)
                    ) {
                        val text = classifier?.let { stringResource(id = R.string.common_save) }
                            ?: stringResource(id = R.string.common_calculate)
                        Text(text = text)
                    }
                    AnimatedVisibility(visible = classifier != null) {
                        Button(onClick = { classifier = null }, modifier = Modifier.weight(0.3f)) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = stringResource(id = R.string.common_reset)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(classifier: BmiClassifier, modifier: Modifier = Modifier) {
    val cardColor =
        if (classifier.getRating() != BmiRating.NormalWeight) warning_color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (classifier.getRating() != BmiRating.NormalWeight) md_theme_dark_onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor, contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.default_padding))
                .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.default_padding)
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = R.string.bmi_calculator_result_text,
                    classifier.weight,
                    classifier.bmiValue
                )
            )
            val icon = painterResource(id = classifier.getRating().drawableRes())
            TextWithIcon(text = stringResource(id = classifier.getRating().stringRes()),
                icon = { Icon(painter = icon, contentDescription = null) })
            if (classifier.getRating() != BmiRating.NormalWeight) {
                Text(
                    text = stringResource(
                        id = R.string.bmi_calculator_ideal_weight_range,
                        classifier.idealWeight[0],
                        classifier.idealWeight[classifier.idealWeight.lastIndex]
                    )
                )
            }
        }
    }

}

@Composable
private fun BmiCalculatorBottomBar(onNavIconClick: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.common_go_to_back)
            )
        }
    })
}

@Suppress("UNCHECKED_CAST")
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BmiCalculatorScreenPreview() {
    GymLogTheme {
        val viewModelFactory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repositoryImpl = BmiInfoRepositoryImpl(AppDataBase_Impl().bmiInfoDao())
                return BmiCalculatorViewModel(repositoryImpl) as T
            }
        }
        val viewModel: BmiCalculatorViewModel = viewModel(factory = viewModelFactory)
        BmiCalculatorScreen(onNavIconClick = {}, onSaved = {}, viewModel = viewModel)
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun ResultCardPreview() {
    GymLogTheme {
        ResultCard(
            classifier = BmiClassifier(
                gender = Gender.Male, weight = 55.0f, height = 176, age = 21
            )
        )
    }
}

