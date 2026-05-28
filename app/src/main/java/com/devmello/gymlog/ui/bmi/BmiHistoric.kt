package com.devmello.gymlog.ui.bmi

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.model.BmiInfo
import com.devmello.gymlog.model.User
import com.devmello.gymlog.ui.bmi.viewmodel.BmiHistoricViewModel
import com.devmello.gymlog.ui.bmi.viewmodel.BmiHistoricViewModelImpl
import com.devmello.gymlog.ui.components.DefaultAlertDialog
import com.devmello.gymlog.ui.components.InfoCard
import com.devmello.gymlog.ui.components.LoadingDialog
import com.devmello.gymlog.ui.components.TextWithIcon
import com.devmello.gymlog.ui.theme.GymLogTheme
import com.devmello.gymlog.ui.theme.md_theme_dark_onPrimary
import com.devmello.gymlog.ui.theme.warning_color
import com.devmello.gymlog.utils.BmiClassifier
import com.devmello.gymlog.utils.BmiRating
import com.devmello.gymlog.utils.Gender
import com.devmello.gymlog.utils.Month
import com.devmello.gymlog.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.TimeZone


@Composable
fun BmiHistoricScreen(
    onError: () -> Unit,
    scaffoldManager: ScaffoldManager,
    modifier: Modifier = Modifier,
    viewModel: BmiHistoricViewModel = koinViewModel<BmiHistoricViewModelImpl>()
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle(State.Loading)
    val user: User? by viewModel.user.collectAsStateWithLifecycle(null)
    var registerToDelete: BmiInfo? by remember { mutableStateOf(null) }
    var showUserCreatorDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    var showBmiCalculatorDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    var showDeleteRegisterDialog: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scaffoldManager.updateConfig(
            ScaffoldConfig(
                fab = {
                    BmiHistoricFloatActionButton(
                        onClickCalculate = { showBmiCalculatorDialog = true })
                },

                )
        )
    }
    when (userState) {
        is State.Loading -> {}

        is State.Success -> {
            (userState as State.Success<User?>).data?.let {
                viewModel.setUser(it)
            } ?: run { showUserCreatorDialog = true }
        }

        is State.Error -> {
            Log.e("on Error", "BmiHistoricScreen: error")
            onError()
        }
    }
    val bmiInfoList by viewModel.getHistoric.collectAsState(emptyList())
    Box {
        if (showDeleteRegisterDialog) DeleteHistoricRegisterConfirmationDialog(
            onDismissRequest = { showDeleteRegisterDialog = false },
            onConfirm = {
                registerToDelete?.let {
                    viewModel.disableBmiInfoRegister(it, onFinished = {
                        showDeleteRegisterDialog = false
                    })
                }
            })
        if (showBmiCalculatorDialog && user != null) BmiCalculatorDialog(
            onDismissRequest = {
                showBmiCalculatorDialog = false
            },
            onSaved = { showBmiCalculatorDialog = false },
            user = user!!
        )
        if (showUserCreatorDialog) UserCreatorDialog(
            onDismiss = {
                user?.let {
                    showUserCreatorDialog = false
                } ?: onError()
            }, onConfirm = { newUser ->
                viewModel.saveUser(newUser) {
                    showUserCreatorDialog = false
                }
            }, userToUpdate = user
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = modifier
            ) {
                user?.let {
                    BmiHistoricHeader(
                        user = it,
                        onClickEdit = { showUserCreatorDialog = true },
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
            }
            if (bmiInfoList.isNotEmpty()) BmiInfoList(
                bmiInfoList = bmiInfoList, onLongClickListener = {
                    registerToDelete = it
                    showDeleteRegisterDialog = true
                }, modifier = Modifier
                    .heightIn(max = dimensionResource(id = R.dimen.default_max_list_height))
            ) else BmiHistoricEmptyListMessage()
        }
    }

}

@Composable
fun DeleteHistoricRegisterConfirmationDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    DefaultAlertDialog(
        title = stringResource(id = R.string.bmi_historic_delete_register_dialog_title),
        text = stringResource(id = R.string.bmi_historic_delete_register_dialog_text),
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}

@Composable
private fun BmiHistoricFloatActionButton(onClickCalculate: () -> Unit) {
    FloatingActionButton(onClick = onClickCalculate) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calculate),
            contentDescription = stringResource(
                id = R.string.bmi_historic_buttom_calculate
            )
        )
    }
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
                val gender =
                    stringResource(id = user.gender?.stringRes() ?: R.string.common_error_message)
                ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                    Text(text = gender)
                    Text(text = stringResource(id = R.string.common_age_suffix, user.age))
                    Text(
                        text = stringResource(
                            id = R.string.bmi_historic_height_place_holder_in_meters,
                            (user.height.toFloat() / 100)
                        )
                    )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BmiInfoList(
    bmiInfoList: List<BmiInfo>,
    onLongClickListener: (BmiInfo) -> Unit,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .animateContentSize()
    ) {
        Text(
            text = stringResource(id = R.string.bmi_historic_title),
            style = MaterialTheme.typography.titleLarge
        )
        for ((key, list) in hashMap.toSortedMap()) {
            val monthName = stringResource(id = Month.entries[key].stringRes())
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = monthName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn {
                    items(list.sortedBy { it.dateInMillis }, key = { it.id }) {
                        Modifier
                            .padding(dimensionResource(id = R.dimen.default_padding))
                        BmiInfoItem(
                            bmiInfo = it,
                            modifier = Modifier.animateItem(
                                fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                            ),
                            onLongClickListener = onLongClickListener
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BmiInfoItem(
    bmiInfo: BmiInfo, modifier: Modifier = Modifier, onLongClickListener: (BmiInfo) -> Unit
) {
    val classifier = BmiClassifier(
        bmiInfo.gender, weight = bmiInfo.weight, height = bmiInfo.height, age = bmiInfo.age
    )
    val cardColor =
        if (classifier.getRating() != BmiRating.NormalWeight) warning_color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (classifier.getRating() != BmiRating.NormalWeight) md_theme_dark_onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = { onLongClickListener(bmiInfo) }),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor, contentColor = contentColor
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
                    .clip(RoundedCornerShape(topEnd = dimensionResource(id = R.dimen.extra_large_corner_size)))
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
                    text = stringResource(id = R.string.bmi_historic_item_day_prefix, day),
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
                    text = stringResource(
                        id = R.string.bmi_historic_item_weight_suffix, bmiInfo.weight
                    ), icon = {
                        if (classifier.getRating() != BmiRating.NormalWeight) {
                            Icon(
                                imageVector = Icons.Rounded.Warning, contentDescription = null
                            )
                        }
                    })

                Text(
                    text = stringResource(
                        id = R.string.bmi_historic_item_bmi_prefix, classifier.bmiValue
                    )
                )
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

@Composable
private fun BmiHistoricEmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Icon(
                painter = painterResource(id = R.drawable.ic_history),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.empty_list_icon_size))
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))
        Text(
            text = "O Histórico esta vazio!",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(showSystemUi = true)
@Composable
private fun BmiHistoricScreenPreview() {
    GymLogTheme {
        val viewModel = object : BmiHistoricViewModel {
            override val userState: Flow<State<User?>> = flow {
                emit(
                    State.Success(
                        User(
                            gender = Gender.Male,
                            height = 176,
                            age = 21
                        )
                    )
                )

            }
            override val user: Flow<User?>
                get() = TODO("Not yet implemented")
            override val currentUser: UserData =
                UserData(uid = "", userName = "Lucas Mello", profilePicture = null)
            override val getHistoric: Flow<List<BmiInfo>>
                get() = emptyFlow()

            override fun setLoading() {
                TODO("Not yet implemented")
            }

            override fun setUser(user: User) {
                TODO("Not yet implemented")
            }

            override fun saveUser(user: User, onSuccess: () -> Unit) {
                TODO("Not yet implemented")
            }

            override fun sync() {
                TODO("Not yet implemented")
            }

            override fun getUser() {
                TODO("Not yet implemented")
            }

            override fun disableBmiInfoRegister(bmiInfo: BmiInfo, onFinished: () -> Unit) {
                TODO("Not yet implemented")
            }
        }
        BmiHistoricScreen(
            onError = {},
            viewModel = viewModel,
            scaffoldManager = ScaffoldManager(),
        )
    }
}