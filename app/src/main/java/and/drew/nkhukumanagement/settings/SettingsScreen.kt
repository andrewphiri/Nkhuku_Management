package and.drew.nkhukumanagement.settings

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.backupAndExport.BackupAndRestoreViewModel
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import and.drew.nkhukumanagement.utils.ShowSuccessfulDialog
import and.drew.nkhukumanagement.utils.getAllCurrenciesInUse
import android.Manifest
import android.app.LocaleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.icu.util.Currency
import android.icu.util.ULocale
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

object SettingsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Settings
    override val route: String
        get() = "settings screen"
    override val resourceId: Int
        get() = R.string.settings
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    userPrefsViewModel: UserPrefsViewModel,
    backupAndRestore: BackupAndRestoreViewModel = hiltViewModel(),
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    navigateToAccountInfoScreen: () -> Unit,
    contentType: ContentType
) {
    val allVaccinationItems by vaccinationViewModel.getAllVaccinationItems.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showSuccessfulAfterRestoreDialog by remember { mutableStateOf(false) }
    var isFileValid by remember { mutableStateOf(false) }

    val userPreferences by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    val allCurrencies = getAllCurrenciesInUse().map { it.value }.toSet().toList()
        .sortedBy { it?.displayName }
    var restoreBackupUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isCircularIndicatorShowing by rememberSaveable { mutableStateOf(false) }
    var key by remember { mutableStateOf("en_zm") }
    var (selectedCurrency, onCurrencySelected) = remember {
        mutableStateOf(
            Currency.getInstance(
                ULocale(userPreferences.currencyLocale)
            )
        )
    }
    var allLocale: List<Locale> by remember { mutableStateOf(listOf()) }
    var selectedLocale by remember { mutableStateOf(Locale(userPreferences.languageLocale)) }
    selectedLocale = Locale(userPreferences.languageLocale)
    var defaultLocale by remember { mutableStateOf(Locale.getDefault()) }
    //Get default system language
    defaultLocale =
        Locale("${ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]?.language}")
    //ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]

    val appLanguages = context.resources.getStringArray(R.array.app_languages)
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedLocale) {
 //       Log.i("Default_Language", selectedLocale.language.toString())
//        Log.i("Default____ARRAYY", appLanguages.toString())
        val languagesList = mutableListOf<Locale>()
        for (language in appLanguages) {
//             Log.i("Default____", ULocale(language).displayLanguage)
            if (defaultLocale == ULocale(language).toLocale() &&
                defaultLocale == selectedLocale &&
                appLanguages.contains(defaultLocale.language)
            ) {
                languagesList.add(0, defaultLocale)
            } else if (
                (defaultLocale == ULocale(language).toLocale()) &&
                (defaultLocale != selectedLocale) &&
                languagesList.isNotEmpty() &&
                appLanguages.contains(defaultLocale.language)
            ) {
                languagesList.add(1, defaultLocale)
            } else if (
                (selectedLocale == ULocale(language).toLocale()) &&
                defaultLocale != selectedLocale &&
                languagesList.isNotEmpty()
            ) {
                languagesList.add(0, selectedLocale)
            } else {
                languagesList.add(ULocale(language).toLocale())
            }
//             Log.i("Default____LOCALE", allLocale.toString())
        }
        allLocale = languagesList
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
            showSuccessfulAfterRestoreDialog = true
        } else {
            showRestoreDialog = true
        }
    }

    val requestVaccineNotificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            coroutineScope.launch {
                userPrefsViewModel.updateNotifications(isGranted)
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.no_vaccine_reminders),
                    duration = SnackbarDuration.Long
                )
            }

        }
    }

    val requestStoragePermission = rememberLauncherForActivityResult(
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
                    message = context.getString(R.string.feature_unavailable),
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
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
                allCurrencies = allCurrencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currency ->

                    for ((k, v) in getAllCurrenciesInUse()) {
                        if (v == currency) {
                            key = k.toLanguageTag()
                            break
                        }
                    }
                    selectedCurrency = currency
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
                            requestStoragePermission.launch(
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
                        showSuccessfulAfterRestoreDialog = true
                    }
                },
                onDismissRestoreBackupDialog = { showRestoreDialog = false },
                onDismissSuccessAlertDialog = { showSuccessfulAfterRestoreDialog = false },
                isSuccessAlertDialogShowing = showSuccessfulAfterRestoreDialog,
                isFileValid = isFileValid,
                receiveNotifications = receiveNotifications,
                onCheckedChange = {
                    userPrefsViewModel.updateNotifications(it)
                    if (!receiveNotifications) {
                        allVaccinationItems.forEach {
                            vaccinationViewModel.cancelNotification(it)
                        }
                    } else {

                        requestVaccineNotificationPermission.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                },
                navigateToAccountInfoScreen = navigateToAccountInfoScreen,
                showLanguageDialog = showLanguageDialog,
                onLanguageSelected = { locale ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.getSystemService(LocaleManager::class.java)
                            .applicationLocales =
                            android.os.LocaleList.forLanguageTags(locale.language)
                    } else {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                locale.toLanguageTag()
                            )
                        )

                    }
                    selectedLocale = locale

                    userPrefsViewModel.updateLanguageLocale(locale.toLanguageTag())

                },
                onDismissLanguageDialog = { showLanguageDialog = false },
                allLocale = allLocale,
                selectedLocale = selectedLocale,
                onShowLanguageDialog = {
                    showLanguageDialog = true
                },
                defaultLocale = defaultLocale,
                onExpand = {
                    expanded = !expanded
                },
                onOptionSelected = {
                    userPrefsViewModel.updateTraySize(it)
                },
                onDismissTraySizeDropDownMenu = {
                    expanded = false
                },
                expanded = expanded,
                traySize = userPreferences.traySize,
                traySizeOptions = listOf("6", "12", "18", "24", "30") ,
            )
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    onShowCurrencyDialog: () -> Unit = {},
    onShowLanguageDialog: () -> Unit = {},
    onDismissCurrencyDialog: () -> Unit = {},
    showCurrencyDialog: Boolean = false,
    allCurrencies: List<Currency?>,
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
    navigateToAccountInfoScreen: () -> Unit,
    onLanguageSelected: (Locale) -> Unit,
    showLanguageDialog: Boolean,
    selectedLocale: Locale,
    defaultLocale: Locale,
    onDismissLanguageDialog: () -> Unit,
    allLocale: List<Locale>,
    traySize: String,
    traySizeOptions: List<String>,
    onOptionSelected: (String) -> Unit,
    onExpand: (Boolean) -> Unit,
    expanded: Boolean,
    onDismissTraySizeDropDownMenu: () -> Unit,
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
            CurrencyPickerDialog(
                showCurrencyDialog = showCurrencyDialog,
                onDismiss = onDismissCurrencyDialog,
                selectedCurrency = selectedCurrency,
                allCurrencies = allCurrencies,
                onCurrencySelected = onCurrencySelected,
                selectedLocale = selectedLocale
            )
            AppLanguagePickerDialog(
                onLanguageSelected = onLanguageSelected,
                onDismiss = onDismissLanguageDialog,
                allLocale = allLocale,
                showLanguageDialog = showLanguageDialog,
                selectedLocale = selectedLocale,
                defaultLocale = defaultLocale
            )
            ShowAlertDialog(
                onDismissAlertDialog = onDismissRestoreBackupDialog,
                onConfirm = onConfirmRestore,
                dismissButtonText = stringResource(R.string.cancel),
                confirmButtonText = stringResource(R.string.restore),
                title = stringResource(R.string.restore_backup),
                message = stringResource(R.string.data_will_be_overwritten_are_you_sure_you_want_to_proceed),
                isAlertDialogShowing = isRestoreBackupDialogShowing
            )

            ShowSuccessfulDialog(
                onDismissSuccessAlertDialog = onDismissSuccessAlertDialog,
                isSuccessAlertDialogShowing = isSuccessAlertDialogShowing,
                isActionSuccessful = isFileValid
            )
            Text(
                text = stringResource(R.string.account),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = navigateToAccountInfoScreen)
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.account_information)
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.app_language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        if (!isCircularIndicatorShowing) {
                            onShowLanguageDialog()
                        }
                    })
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.default_label)
                )
                Text(
                    text = if (selectedLocale == defaultLocale)
                        "${selectedLocale.getDisplayLanguage(selectedLocale)} ${stringResource(R.string.device_s_language)}" else
                        selectedLocale.getDisplayLanguage(selectedLocale)
                            .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
            }
            HorizontalDivider(
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.currency),
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
                    text = stringResource(R.string.default_label)
                )
                Text(
                    text = "${selectedCurrency?.symbol} - ${
                        selectedCurrency?.getDisplayName(
                            selectedLocale
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
            }

            HorizontalDivider(
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.backup_and_restore),
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
                    text = stringResource(R.string.create_backup)
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
                    text = stringResource(R.string.restore)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.notifications),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(weight = 0.8f, fill = true),
                    text = stringResource(R.string.vaccination_reminders)
                )
                Switch(
                    modifier = Modifier
                        .size(16.dp)
                        .weight(0.2f),
                    checked = receiveNotifications,
                    onCheckedChange = onCheckedChange
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = Dp.Hairline,
                color = Color.DarkGray
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.other),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            DropDownMenuDialog(
                value = traySize,
                onDismissed = onDismissTraySizeDropDownMenu,
                options = traySizeOptions,
                onOptionSelected = onOptionSelected ,
                onExpand = onExpand,
                label = stringResource(R.string.tray_size),
                expanded = expanded,
            )

        }
    }
}

@Composable
fun CurrencyPickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    showCurrencyDialog: Boolean,
    allCurrencies: List<Currency?>,
    selectedCurrency: Currency?,
    onCurrencySelected: (Currency?) -> Unit,
    selectedLocale: Locale
) {
    if (showCurrencyDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            ElevatedCard(
                modifier = modifier
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxHeight(0.95f)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.choose_default_currency),
                            style = MaterialTheme.typography.titleSmall
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = Dp.Hairline,
                            color = Color.DarkGray
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 8.dp),
                            modifier = Modifier.padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    Row(

                                    ) {
                                        RadioButton(
                                            selected = currency?.equals(selectedCurrency) == true,
                                            onClick = null
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 16.dp),
                                            text = "${currency?.symbol} - ${
                                                currency?.getDisplayName(
                                                    selectedLocale
                                                )
                                            }"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        HorizontalDivider(
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
                            text = stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                }

            }
        }

    }
}

@Composable
fun AppLanguagePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    showLanguageDialog: Boolean,
    allLocale: List<Locale>,
    selectedLocale: Locale,
    defaultLocale: Locale,
    onLanguageSelected: (Locale) -> Unit
) {
    if (showLanguageDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            ElevatedCard(
                modifier = modifier
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxHeight(0.95f)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = stringResource(R.string.choose_default_language),
                            style = MaterialTheme.typography.titleSmall
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = Dp.Hairline,
                            color = Color.DarkGray
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 8.dp),
                            modifier = Modifier.padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(allLocale) { locale ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .selectable(
                                            selected = (
                                                    locale.equals(selectedLocale)),
                                            onClick = {
                                                onLanguageSelected(locale)
                                                onDismiss()
                                            },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = locale.equals(selectedLocale),
                                        onClick = null
                                    )
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(start = 16.dp),
                                            text = locale.getDisplayLanguage(locale)
                                                .replaceFirstChar { it.uppercase() }
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 16.dp),
                                            fontWeight = FontWeight.Light,
                                            text = if (locale.language == defaultLocale.language)
                                                stringResource(R.string.device_s_language) else
                                                locale.getDisplayLanguage(selectedLocale)
                                        )
                                    }

                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        HorizontalDivider(
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
                            text = stringResource(R.string.cancel),
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