package com.example.gymlog.ui.bmi

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.gymlog.R
import com.example.gymlog.extensions.checkConnection
import com.example.gymlog.model.BmiInfo
import com.example.gymlog.model.User
import com.example.gymlog.ui.auth.authclient.UserData
import com.example.gymlog.ui.bmi.viewmodel.BmiHistoricViewModel
import com.example.gymlog.ui.bmi.viewmodel.BmiHistoricViewModelImpl
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.InfoCard
import com.example.gymlog.ui.components.LoadingDialog
import com.example.gymlog.ui.components.TextWithIcon
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.ui.theme.md_theme_dark_onPrimary
import com.example.gymlog.ui.theme.warning_color
import com.example.gymlog.utils.BmiClassifier
import com.example.gymlog.utils.BmiRating
import com.example.gymlog.utils.Gender
import com.example.gymlog.utils.Month
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.TimeZone


@Composable
fun BmiHistoricScreen(
    onError: () -> Unit,
    viewModel: BmiHistoricViewModel = koinViewModel<BmiHistoricViewModelImpl>(),
    onNavIconClick: () -> Unit
) {
    val context = LocalContext.current
    var isLoading: Boolean by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val userResource = viewModel.userResource.collectAsState(Resource.Loading)
    var user: User? by remember { mutableStateOf(null) }
    var registerToDelete: BmiInfo? by remember { mutableStateOf(null) }
    var showUserCreatorDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    var showBmiCalculatorDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    var showDeleteRegisterDialog: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        context.checkConnection(onNotConnected = {
            Log.d("TAG", "BmiHistoricScreen: Not Connected")
            isLoading = true
            viewModel.getUser()
            isLoading = false
        }) {
            Log.d("TAG", "BmiHistoricScreen: Connected")
            isLoading = true
            viewModel.sync()
            viewModel.getUser()
            isLoading = false
        }
    }
    when (userResource.value) {
        is Resource.Loading -> {
            isLoading = true
        }

        is Resource.Success -> {
            (userResource.value as Resource.Success<User?>).data?.let {
                user = it
                isLoading = false
            } ?: run { showUserCreatorDialog = true }

        }

        else -> {
            onError()
        }
    }
    val bmiInfoList by viewModel.getHistoric.collectAsState(emptyList())
    Scaffold(bottomBar = {
        BmiHistoricBottomBar(onNavIconClick = onNavIconClick,
            onClickCalculate = { showBmiCalculatorDialog = true })
    }) { paddingValues ->
        Box {
            if (showDeleteRegisterDialog) DeleteHistoricRegisterConfirmationDialog(
                onDismissRequest = { showDeleteRegisterDialog = false },
                onConfirm = {
                    registerToDelete?.let {
                        scope.launch {
                            isLoading = true
                            viewModel.disableBmiInfoRegister(it)
                            isLoading = false
                        }
                        showDeleteRegisterDialog = false
                    }
                })
            if (showBmiCalculatorDialog && user != null) BmiCalculatorDialog(
                onDismissRequest = {
                    showBmiCalculatorDialog = false
                },
                onSaved = { showBmiCalculatorDialog = false },
                user = user!!
            )
            if (showUserCreatorDialog) UserCreatorDialog(onDismiss = {
                user?.let {
                    showUserCreatorDialog = false
                } ?: onError()
            }, onConfirm = { newUser ->
                scope.launch {
                    viewModel.setLoading()
                    viewModel.saveUser(newUser)
                    isLoading = false
                }
                showUserCreatorDialog = false
            }, userToUpdate = user
            )
            if (isLoading) LoadingDialog()
            ConstraintLayout(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val (header, historic) = createRefs()
                Column(
                    modifier = Modifier
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                        }
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
                        .constrainAs(historic) {
                            top.linkTo(header.bottom)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .heightIn(max = dimensionResource(id = R.dimen.default_max_list_height))
                ) else BmiHistoricEmptyListMessage(modifier = Modifier.constrainAs(historic) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.preferredWrapContent
                })
            }
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
private fun BmiHistoricBottomBar(onNavIconClick: () -> Unit, onClickCalculate: () -> Unit) {
    BottomAppBar(floatingActionButton = {
        FloatingActionButton(onClick = onClickCalculate) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calculate),
                contentDescription = stringResource(
                    id = R.string.bmi_historic_buttom_calculate
                )
            )
        }
    }, actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(id = R.string.common_go_to_back)
            )
        }
    })
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
            val monthName = stringResource(id = Month.values()[key].stringRes())
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
                        BmiInfoItem(
                            bmiInfo = it,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.default_padding))
                                .animateItemPlacement(),
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
                TextWithIcon(text = stringResource(
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
                TextWithIcon(text = stringResource(id = classifier.getRating().stringRes()),
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
            override val userResource: Flow<Resource<User?>> = flow {
                emit(
                    Resource.Success(
                        User(
                            gender = Gender.Male,
                            height = 176,
                            age = 21
                        )
                    )
                )

            }
            override val currentUser: UserData =
                UserData(uid = "", userName = "Lucas Mello", profilePicture = null)
            override val getHistoric: Flow<List<BmiInfo>>
                get() = emptyFlow()

            override fun setLoading() {
                TODO("Not yet implemented")
            }

            override suspend fun saveUser(user: User) {
                TODO("Not yet implemented")
            }

            override suspend fun sync() {
                TODO("Not yet implemented")
            }

            override suspend fun getUser() {
                TODO("Not yet implemented")
            }

            override suspend fun disableBmiInfoRegister(bmiInfo: BmiInfo) {
                TODO("Not yet implemented")
            }
        }
        BmiHistoricScreen(
            onNavIconClick = {},
            onError = {},
            viewModel = viewModel,
        )
    }
}