package com.example.gymlog.ui.bmi

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.User
import com.example.gymlog.ui.bmi.viewmodel.BmiHistoricViewModel
import com.example.gymlog.ui.components.InfoCard
import com.example.gymlog.ui.components.TextWithIcon
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.ui.theme.md_theme_dark_onPrimary
import com.example.gymlog.ui.theme.warning_color
import com.example.gymlog.utils.BmiClassifier
import com.example.gymlog.utils.BmiRating
import com.example.gymlog.utils.Month
import com.example.gymlog.utils.Resource
import com.example.gymlog.utils.Gender
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


@Composable
fun BmiHistoricScreen(
    onNavIconClick: () -> Unit,
    onError: () -> Unit,
    viewModel: BmiHistoricViewModel = koinViewModel()
) {
    var isLoading: Boolean by remember { mutableStateOf(false) }
    viewModel.getUser()
    val scope = rememberCoroutineScope()
    val userResource = viewModel.userResource.collectAsState(Resource.Loading)
    var user: User? by remember { mutableStateOf(null) }
    var showUserCreator: Boolean by rememberSaveable { mutableStateOf(false) }
    when (userResource.value) {
        is Resource.Loading -> {
            isLoading = true
        }

        is Resource.Success -> {
            (userResource.value as Resource.Success<User?>).data?.let {
                user = it
                isLoading = false
            } ?: run { showUserCreator = true }

        }

        else -> {
            onError()
        }
    }
    val bmiInfoList by viewModel.getHistoric.collectAsState(emptyList())
    Scaffold(bottomBar = {
        BmiHistoricBottomBar(
            onNavIconClick = onNavIconClick,
            onClickCalculate = {})
    }) { paddingValues ->
        Box {
            if (showUserCreator) UserCreatorDialog(
                onDismiss = {
                    user?.let {
                        showUserCreator = false
                    } ?: onError()
                },
                onConfirm = { newUser ->
                    scope.launch {
                        viewModel.setLoading()
                        viewModel.saveUser(newUser)
                        showUserCreator = false
                        isLoading = false
                    }
                },
                userToupdate = user
            )
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                user?.let {
                    BmiHistoricHeader(
                        user = it,
                        onClickEdit = { showUserCreator = true },
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.default_padding)
                        )
                    )
                }
                InfoCard(
                    text = stringResource(id = R.string.bmi_historic_information),
                    modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.default_padding)
                    )
                )
                BmiInfoList(bmiInfoList = bmiInfoList, modifier = Modifier.heightIn(max = 500.dp))
            }
        }
    }
}

@Composable
private fun BmiHistoricBottomBar(onNavIconClick: () -> Unit, onClickCalculate: () -> Unit) {
    BottomAppBar(
        floatingActionButton = {
            FloatingActionButton(onClick = onClickCalculate) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calculate),
                    contentDescription = stringResource(
                        id = R.string.bmi_historic_buttom_calculate
                    )
                )
            }
        },
        actions = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.common_go_to_back)
                )
            }
        }
    )
}

@Composable
fun BmiHistoricHeader(user: User, onClickEdit: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(dimensionResource(id = R.dimen.default_padding))
            ) {
                val gender = when (user.gender) {
                    Gender.Male -> "Homem"
                    Gender.Female -> "Mulher"
                }
                ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                    Text(text = gender)
                    Text(text = "${user.age} anos")
                    Text(text = "Altura: ${user.height.toFloat() / 100} m")
                }
            }
            IconButton(onClick = onClickEdit, modifier = Modifier.weight(0.2f)) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(id = R.string.common_edit)
                )
            }
        }
    }
}

@Composable
fun BmiInfoList(bmiInfoList: List<BmiInfo>, modifier: Modifier = Modifier) {
    val hashMap: HashMap<Int, MutableList<BmiInfo>> = HashMap()
    bmiInfoList.forEach { bmiInfo ->
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            timeInMillis = bmiInfo.dateInMillis
        }
        val month = calendar.get(Calendar.MONTH)
        if (!hashMap.containsKey(month)) {
            val list = mutableListOf(bmiInfo)
            hashMap[month] = list
        } else {
            val list = hashMap[month]
            list?.let {
                list.add(bmiInfo)
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(
            dimensionResource(id = R.dimen.default_padding)
        )
    ) {
        for ((key, list) in hashMap.toSortedMap()) {
            val monthName = stringResource(id = Month.values()[key].stringRes())
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))) {
                Text(
                    text = monthName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn {
                    items(list.sortedBy { it.dateInMillis }, key = { it.id }) {
                        BmiInfoItem(
                            bmiInfo = it,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BmiInfoItem(bmiInfo: BmiInfo, modifier: Modifier = Modifier) {
    val classifier = BmiClassifier(
        bmiInfo.gender,
        weight = bmiInfo.weight,
        height = bmiInfo.height,
        age = bmiInfo.age
    )
    val cardColor =
        if (classifier.getRating() != BmiRating.NormalWeight) warning_color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (classifier.getRating() != BmiRating.NormalWeight) md_theme_dark_onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    max = 80.dp
                )
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(0.7f))
                    .fillMaxHeight()
            ) {
                val calendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault()
                    timeInMillis = bmiInfo.dateInMillis
                }
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val dayTextColor =
                    if (classifier.getRating() != BmiRating.NormalWeight) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
                Text(
                    text = "Dia $day",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            dimensionResource(id = R.dimen.default_padding)
                        ),
                    style = MaterialTheme.typography.displaySmall,
                    color = dayTextColor
                )
            }
            Column(
                horizontalAlignment = Alignment.End, modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        dimensionResource(id = R.dimen.default_padding)
                    )
            ) {
                TextWithIcon(
                    text = " %.${1}f kg".format(bmiInfo.weight),
                    icon = {
                        if (classifier.getRating() != BmiRating.NormalWeight) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null
                            )
                        }
                    })

                Text(text = "Indice de Massa Corporal: %.${1}f".format(classifier.bmiValue))
                TextWithIcon(
                    text = stringResource(id = classifier.getRating().stringRes()),
                    icon = {
                        val icon = painterResource(id = classifier.getRating().drawableRes())
                        Icon(painter = icon, contentDescription = null)
                    })
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun BmiInfoListPreview() {
    GymLogTheme {
        val list = listOf(
            BmiInfo(
                gender = Gender.Male,
                weight = 66f,
                height = 176,
                age = 21,
                dateInMillis = Date().time
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 66f,
                height = 176,
                age = 21,
                dateInMillis = 1686020400000
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 55f,
                height = 176,
                age = 21,
                dateInMillis = 1677639600000
            ),
            BmiInfo(
                gender = Gender.Male,
                weight = 55f,
                height = 176,
                age = 21,
                dateInMillis = 1677726000000
            )
        )
        Surface() {
            BmiInfoList(bmiInfoList = list)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview()
@Composable
private fun BmiHistoricItemPreview() {
    GymLogTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            val bmiInfo =
                BmiInfo(
                    gender = Gender.Male,
                    weight = 66f,
                    height = 176,
                    age = 21,
                    dateInMillis = Date().time
                )
            BmiInfoItem(bmiInfo = bmiInfo)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun BmiHistoricHeaderPreview() {
    GymLogTheme {
        val person = User(gender = Gender.Male, height = 176, age = 21)
        BmiHistoricHeader(user = person, onClickEdit = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(showSystemUi = true)
@Composable
private fun BmiHistoricScreenPreview() {
    GymLogTheme {
        BmiHistoricScreen(onNavIconClick = {}, onError = {})
    }
}