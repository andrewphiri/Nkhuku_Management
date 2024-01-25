package and.drew.nkhukumanagement.settings

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.backup.BackupAndRestoreViewModel
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import and.drew.nkhukumanagement.utils.ShowSuccessfulDialog
import and.drew.nkhukumanagement.utils.getAllCurrenciesInUse
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Currency
import android.icu.util.ULocale
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SettingsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Settings
    override val route: String
        get() = "settings screen"
    override val resourceId: Int
        get() = R.string.settings
}

@Composable
fun SettingsScreen(
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    userPrefsViewModel: UserPrefsViewModel,
    backupAndRestore: BackupAndRestoreViewModel = hiltViewModel(),
    navigateToAccountInfoScreen: () -> Unit,
    contentType: ContentType
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showSuccesfulAfterRestoreDialog by remember { mutableStateOf(false) }
    var isFileValid by remember { mutableStateOf(false) }
    val userPreferences by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    var restoreBackupUri by remember { mutableStateOf<Uri?>(null) }
    var snackbarHostState = remember { SnackbarHostState() }
    var isCircularIndicatorShowing by rememberSaveable { mutableStateOf(false) }
    var key by remember { mutableStateOf("en_zm") }
    var (selectedCurrency, onCurrencySelected) = remember {
        mutableStateOf(
            Currency.getInstance(
                ULocale(userPreferences.currencyLocale)
            )
        )
    }
    var receiveNotifications by remember { mutableStateOf(true) }
    receiveNotifications = userPreferences.receiveNotifications
    selectedCurrency = Currency.getInstance(
        ULocale(userPreferences.currencyLocale)
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        restoreBackupUri = it.data?.data
        isFileValid = backupAndRestore.isFileValid(restoreBackupUri)
        if (!isFileValid) {
            showSuccesfulAfterRestoreDialog = true
        } else {
            showRestoreDialog = true
        }
    }

    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                isCircularIndicatorShowing = true
                delay(3000)
                backupAndRestore.backupAndShareFile()
            }.invokeOnCompletion {
                isCircularIndicatorShowing = false
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "This feature is unavailable because it requires access to the phone's storage.",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(SettingsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SettingsCard(
                showCurrencyDialog = showCurrencyDialog,
                onShowCurrencyDialog = {
                    showCurrencyDialog = true
                },
                onDismissCurrencyDialog = { showCurrencyDialog = false },
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currency ->

                    for ((k, v) in getAllCurrenciesInUse()) {
                        if (v == currency) {
                            key = k.toLanguageTag()
                            break
                        }
                    }
                    userPrefsViewModel.updateCurrency(currency, key)
                    onCurrencySelected(currency)

                },
                onClickBackup = {

                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) -> {
                            coroutineScope.launch {
                                isCircularIndicatorShowing = true
                                delay(3000)
                                backupAndRestore.backupAndShareFile()
                            }.invokeOnCompletion {
                                isCircularIndicatorShowing = false
                            }
                        }

                        else -> {
                            requestPermission.launch(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                    }


                },
                isCircularIndicatorShowing = isCircularIndicatorShowing,
                onClickRestore = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .apply {
                            type = "application/octet-stream"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                    launcher.launch(intent)
                },
                isRestoreBackupDialogShowing = showRestoreDialog,
                onConfirmRestore = {
                    showRestoreDialog = false
                    coroutineScope.launch {
                        isCircularIndicatorShowing = true
                        delay(2000)
                        backupAndRestore.restoreBackUp(restoreBackupUri)
                        isCircularIndicatorShowing = false
                    }.invokeOnCompletion {
                        showSuccesfulAfterRestoreDialog = true
                    }
                },
                onDismissRestoreBackupDialog = { showRestoreDialog = false },
                onDismissSuccessAlertDialog = { showSuccesfulAfterRestoreDialog = false },
                isSuccessAlertDialogShowing = showSuccesfulAfterRestoreDialog,
                isFileValid = isFileValid,
                receiveNotifications = receiveNotifications,
                onCheckedChange = {
                    userPrefsViewModel.updateNotifications(it)
                },
                navigateToAccountInfoScreen = navigateToAccountInfoScreen
            )
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    onShowCurrencyDialog: () -> Unit = {},
    onDismissCurrencyDialog: () -> Unit = {},
    showCurrencyDialog: Boolean = false,
    onCurrencySelected: (Currency?) -> Unit,
    selectedCurrency: Currency?,
    onClickBackup: () -> Unit,
    onClickRestore: () -> Unit,
    isCircularIndicatorShowing: Boolean,
    onConfirmRestore: () -> Unit,
    isRestoreBackupDialogShowing: Boolean,
    onDismissRestoreBackupDialog: () -> Unit,
    onDismissSuccessAlertDialog: () -> Unit,
    isSuccessAlertDialogShowing: Boolean,
    isFileValid: Boolean,
    receiveNotifications: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    navigateToAccountInfoScreen: () -> Unit
) {

    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (isCircularIndicatorShowing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isCircularIndicatorShowing) 0.5f else 1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencyPickerDialog(
                showDialog = showCurrencyDialog,
                onDismiss = onDismissCurrencyDialog,
                selectedCurrency = selectedCurrency,
                allCurrencies = getAllCurrenciesInUse().map { it.value }.toSet().toList()
                    .sortedBy { it?.displayName },
                onCurrencySelected = onCurrencySelected
            )
            ShowAlertDialog(
                onDismissAlertDialog = onDismissRestoreBackupDialog,
                onConfirm = onConfirmRestore,
                dismissButtonText = "Cancel",
                confirmButtonText = "Restore",
                title = "Restore Backup",
                message = "Data will be overwritten. Are you sure you want to proceed?",
                isAlertDialogShowing = isRestoreBackupDialogShowing
            )

            ShowSuccessfulDialog(
                onDismissSuccessAlertDialog = onDismissSuccessAlertDialog,
                isSuccessAlertDialogShowing = isSuccessAlertDialogShowing,
                isActionSuccessful = isFileValid
            )
            Text(
                text = "ACCOUNT",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = navigateToAccountInfoScreen)
                    .padding(vertical = 8.dp),
                text = "Account Information"
            )

            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "CURRENCY",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        if (!isCircularIndicatorShowing) {
                            onShowCurrencyDialog()
                        }
                    })
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Default"
                )
                Text(
                    text = "${selectedCurrency?.symbol} - ${selectedCurrency?.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
            }
            Divider(
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "BACKUP AND RESTORE",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            if (!isCircularIndicatorShowing) {
                                onClickBackup()
                            }
                        })
                        .padding(vertical = 8.dp),
                    text = "Create backup"
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(onClick = {
                            if (!isCircularIndicatorShowing) {
                                onClickRestore()
                            }
                        })
                        .padding(vertical = 8.dp),
                    text = "Restore"
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "NOTIFICATIONS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(weight = 0.8f, fill = true),
                    text = "Vaccination reminders"
                )
                Switch(
                    modifier = Modifier
                        .size(16.dp)
                        .weight(0.2f),
                    checked = receiveNotifications,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}

@Composable
fun currencyPickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    showDialog: Boolean,
    allCurrencies: List<Currency?>,
    selectedCurrency: Currency?,
    onCurrencySelected: (Currency?) -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            ElevatedCard(
                modifier = modifier
            ) {
                Box() {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxHeight(0.95f)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Choose default currency",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = Dp.Hairline,
                            color = Color.DarkGray
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 8.dp),
                            modifier = Modifier.padding(bottom = 24.dp),
                        ) {
                            items(allCurrencies) { currency ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .selectable(
                                            selected = (
                                                    currency?.equals(selectedCurrency) == true),
                                            onClick = {
                                                if (currency != null) {
                                                    onCurrencySelected(currency)
                                                }
                                                onDismiss()
                                            },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = currency?.equals(selectedCurrency) == true,
                                        onClick = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 16.dp),
                                        text = "${currency?.symbol} - ${currency?.displayName}"
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            thickness = Dp.Hairline,
                            color = Color.DarkGray
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                                .align(Alignment.End)
                                .clickable(onClick = onDismiss),
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                }

            }
        }

    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    NkhukuManagementTheme {
//        SettingsCard()
    }
}