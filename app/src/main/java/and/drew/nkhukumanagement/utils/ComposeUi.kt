package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Account
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.ui.theme.Shapes
import and.drew.nkhukumanagement.userinterface.navigation.TabScreens
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

@Composable
fun SignInGoogleButton(
    text: String,
    loadingText: String,
    contentDescription: String,
    icon: Painter,
    shape: Shape = Shapes.medium,
    borderColor: Color = Color.LightGray,
    isLoading: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(
            enabled = !isLoading,
            onClick = onClick
        ),
        shape = shape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isLoading) loadingText else text
            )

            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    tabs: List<TabScreens>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    TabRow(selectedTabIndex = pagerState.currentPage) {
        tabs.forEachIndexed { index, tabItem ->
            Tab(
                modifier = Modifier
                    .background(
                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else
                            Color.Unspecified
                    ),
                text = { Text(text = context.getString(tabItem.title)) },
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = LocalContentColor.current,
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerDateDialog(
    showDialog: Boolean,
    label: String,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    date: String,
    saveDateSelected: (DatePickerState) -> String?,
    datePickerState: DatePickerState,
    onValueChanged: (String) -> Unit,
    isEditable: Boolean = true
) {
    saveDateSelected(datePickerState)?.let { onValueChanged(it) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = date,
        onValueChange = {
            //onValueChanged(it)
//            saveDateSelected(datePickerState)
        },
        label = { Text(text = label) },
        singleLine = true,
        readOnly = true,
        enabled = isEditable,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified
        ),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            updateShowDialogOnClick(showDialog)
                        }
                    }
                }
            }
    )
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissed,
            confirmButton = {
                Button(
                    onClick = onDismissed
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                Button(onClick = onDismissed) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier,
                showModeToggle = false,
            )
        }
    }
}

@Composable
fun AddNewEntryDialog(
    modifier: Modifier = Modifier,
    entry: String,
    showDialog: Boolean,
    onValueChanged: (String) -> Unit,
    onDismissed: () -> Unit,
    onSaveEntry: () -> Unit,
    isEnabled: Boolean = false,
    label: String
) {

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissed
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    modifier = modifier,
                    value = entry,
                    onValueChange = onValueChanged,
                    label = { Text(label) }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary),
                        onClick = onDismissed
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = stringResource(R.string.cancel),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = isEnabled,
                        onClick = onSaveEntry
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = stringResource(R.string.save),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuDialog(
    modifier: Modifier = Modifier,
    value: String,
    expanded: Boolean, onExpand: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    onDismissed: () -> Unit,
    isEditable: Boolean = true,
    options: List<String>,
    label: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = if (isEditable) expanded else false,
            onExpandedChange = onExpand
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                textStyle = MaterialTheme.typography.bodySmall,
                readOnly = true,
                value = value,
                onValueChange = {
                    onOptionSelected(it)
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                isError = value.isBlank(),
                trailingIcon = {
                    if (isEditable) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(
                        LocalContentColor.current.alpha
                    )
                ),
                enabled = isEditable
            )

            ExposedDropdownMenu(
                modifier = Modifier.exposedDropdownSize(true),
                expanded = if (!isEditable and expanded) false else expanded,
                onDismissRequest = onDismissed
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.semantics { contentDescription = option },
                        text = { Text(text = option) },
                        onClick = {
                            onOptionSelected(option)
                            onExpand(false)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuAutoCompleteDialog(
    modifier: Modifier = Modifier,
    value: String,
    expanded: Boolean, onExpand: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    onDismissed: () -> Unit,
    isEditable: Boolean = true,
    options: List<String>,
    label: String,
) {
    val filterOptions = options.filter { it.contains(value, ignoreCase = true) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = if (isEditable) expanded else false,
            onExpandedChange = onExpand
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                textStyle = MaterialTheme.typography.bodySmall,
                value = value,
                onValueChange = {
                    onOptionSelected(it)
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                isError = value.isBlank(),
//                trailingIcon = {
//                    if (isEditable) {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                    }
//                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(
                        LocalContentColor.current.alpha
                    )
                ),
                enabled = isEditable
            )

            if (value.isNotBlank() && filterOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    modifier = Modifier.exposedDropdownSize(true),
                    expanded = if (!isEditable and expanded) false else expanded,
                    onDismissRequest = onDismissed
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            modifier = Modifier.semantics { contentDescription = option },
                            text = { Text(text = option) },
                            onClick = {
                                onOptionSelected(option)
                                onExpand(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BaseAccountRow(
    modifier: Modifier = Modifier,
    labelA: String,
    titleA: String,
    labelB: String,
    titleB: String,
    weightForLabelA: Float = 1f,
    weightForTitleA: Float = 1f,
    weightForLabelB: Float = 1f,
    weightForTitleB: Float = 1f,
    fontWeightForLabel: FontWeight = FontWeight.Bold
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(weightForLabelA),
            text = labelA,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = fontWeightForLabel
        )

        VerticalDivider(
            modifier = Modifier
                .weight(0.01f)
                .fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        Text(
            modifier = Modifier.weight(weightForTitleA),
            text = titleA,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )


        Text(
            modifier = Modifier.weight(weightForLabelB),
            text = labelB,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = fontWeightForLabel
        )

        VerticalDivider(
            modifier = Modifier
                .weight(0.01f)
                .fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        Text(
            modifier = Modifier.weight(weightForTitleB),
            text = titleB,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun BaseSingleRowDetailsItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    weightA: Float = 1f,
    weightB: Float = 1f,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    color: Color = MaterialTheme.colorScheme.tertiary
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.weight(weight = weightA, fill = true),
            text = label,
            style = style,
            textAlign = TextAlign.Start
        )

        VerticalDivider(
            modifier = Modifier
                .weight(0.01f)
                .fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        Text(
            modifier = Modifier.weight(weight = weightB, fill = true),
            text = value,
            style = style,
            color = color,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun BaseSingleRowItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    styleForLabel: TextStyle = MaterialTheme.typography.bodySmall,
    styleForTitle: TextStyle = MaterialTheme.typography.bodySmall,
    colorForLabel: Color = LocalContentColor.current,
    colorForTitle: Color = MaterialTheme.colorScheme.tertiary,
    textAlignA: TextAlign = TextAlign.Start,
    textAlignB: TextAlign = TextAlign.Start,
    weightA: Float = 0.4f,
    weightB: Float = 1f
) {
    Row(modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            modifier = Modifier.weight(weight = weightA),
            text = label,
            style = styleForLabel,
            fontWeight = FontWeight.Bold,
            textAlign = textAlignA,
            color = colorForLabel
        )

        VerticalDivider(
            modifier = Modifier
                .weight(0.01f)
                .fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        Text(
            modifier = Modifier.weight(weight = weightB, fill = true),
            text = value,
            style = styleForTitle,
            textAlign = textAlignB,
            color = colorForTitle
        )
    }
}

/**
 * Filter overflow menu
 */
@Composable
fun ShowFilterOverflowMenu(
    modifier: Modifier = Modifier,
    isOverflowMenuExpanded: Boolean = false,
    onClickActive: () -> Unit,
    onClickInactive: () -> Unit,
    onDismiss: () -> Unit,
    onClickAll: () -> Unit = {},

    ) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DropdownMenu(
            modifier = modifier,
            expanded = isOverflowMenuExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.all)) },
                onClick = onClickAll
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.active)) },
                onClick = onClickActive
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.closed)) },
                onClick = onClickInactive
            )
        }
    }
}

@Composable
fun ShowOverflowMenu(
    modifier: Modifier = Modifier,
    flock: Flock,
    isOverflowMenuExpanded: Boolean = false,
    isAlertDialogShowing: Boolean = false,
    onDismissAlertDialog: () -> Unit = {},
    onShowMenu: (Int) -> Unit = {},
    onShowAlertDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {},
    title: String,
    message: String,
    showCloseButton: Boolean = true
) {
    ShowAlertDialog(
        onDismissAlertDialog = onDismissAlertDialog,
        onConfirm = onDelete,
        isAlertDialogShowing = isAlertDialogShowing,
        title = title,
        message = message
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = { onShowMenu(flock.id) }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Show overflow menu"
            )
        }
        DropdownMenu(
            modifier = modifier,
            expanded = isOverflowMenuExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    onShowAlertDialog()
                    onDismiss()
                }
            )
            if (showCloseButton) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (flock.active) stringResource(R.string.close) else stringResource(
                                R.string.reopen
                            )
                        )
                    },
                    onClick = {
                        onClose()
                        onDismiss()
                    }
                )
            }
        }
    }
}

/**
 * Success dialog
 */
@Composable
fun ShowSuccessfulDialog(
    modifier: Modifier = Modifier,
    onDismissSuccessAlertDialog: () -> Unit,
    isSuccessAlertDialogShowing: Boolean,
    title: String = stringResource(R.string.restore_successful),
    failureTitle: String = stringResource(R.string.restore_failed),
    dismissSuccessButtonText: String = stringResource(R.string.close),
    fileValidMessage: String = stringResource(R.string.this_file_is_not_supported_please_choose_a_valid_file),
    animDuration: Int = 1000,
    isActionSuccessful: Boolean = true
) {

    val animateRotation by animateFloatAsState(
        targetValue = if (isSuccessAlertDialogShowing) -90f * 12f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )
    if (isSuccessAlertDialogShowing) {
        Dialog(
            onDismissRequest = onDismissSuccessAlertDialog,
            properties = DialogProperties()
        ) {
            ElevatedCard {
                Column(
                    modifier = modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (isActionSuccessful) title else failureTitle,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    if (isActionSuccessful) {
                        Image(
                            modifier = Modifier
                                .size(100.dp)
                                .rotate(animateRotation),
                            imageVector = Icons.Default.Check,
                            contentDescription = title
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = fileValidMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = onDismissSuccessAlertDialog
                        ) {
                            Text(
                                modifier = Modifier,
                                text = dismissSuccessButtonText
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Alert dialog when trying to delete an item
 */
@Composable
fun ShowAlertDialog(
    modifier: Modifier = Modifier,
    onDismissAlertDialog: () -> Unit,
    onConfirm: () -> Unit,
    isAlertDialogShowing: Boolean,
    title: String,
    message: String,
    confirmButtonText: String = stringResource(R.string.delete),
    dismissButtonText: String = stringResource(R.string.cancel)
) {
    if (isAlertDialogShowing) {
        AlertDialog(
            modifier = modifier,
            shape = ShapeDefaults.Medium,
            title = { Text(text = title) },
            text = { Text(text = message) },
            onDismissRequest = onDismissAlertDialog,
            dismissButton = {
                OutlinedButton(onClick = onDismissAlertDialog) { Text(text = dismissButtonText) }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(confirmButtonText)
                }
            }
        )
    }
}

@Composable
fun OverflowMenu(
    modifier: Modifier = Modifier,
    isOverflowMenuExpanded: Boolean = false,
    isAlertDialogShowing: Boolean = false,
    onDismissAlertDialog: () -> Unit = {},
    onShowMenu: () -> Unit = {},
    onShowAlertDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    dropDownMenuItemLabel: String,
    title: String,
    message: String
) {

    ShowAlertDialog(
        onDismissAlertDialog = onDismissAlertDialog,
        onConfirm = onDelete,
        isAlertDialogShowing = isAlertDialogShowing,
        title = title,
        message = message
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = onShowMenu
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Show overflow menu"
            )
        }
        DropdownMenu(
            modifier = modifier,
            expanded = isOverflowMenuExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = dropDownMenuItemLabel
                    )
                },
                text = { Text(text = dropDownMenuItemLabel) },
                onClick = onShowAlertDialog
            )
        }
    }
}

@Composable
fun BaseSignInRow(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    readonly: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        readOnly = readonly,
        enabled = enabled,
        onValueChange = onValueChanged,
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = placeholder
            )
        },
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun BaseSignInPassword(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityClicked: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChanged,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = placeholder
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onPasswordVisibilityClicked
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password"
                    else "Show password"
                )
            }
        },
        keyboardOptions = keyboardOptions
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    description: String,
    label: String,
    imageVector: ImageVector,
) {
    ElevatedCard(
        modifier = modifier
            .semantics { contentDescription = description + label }
            .clickable(onClick = onCardClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = imageVector,
                contentDescription = description,
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = label,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius: Float = 200f,
    innerRadius: Float = 100f,
    transparentWidth: Float = 25f,
    input: List<Account>,
    centerText: String = "",
    radiusOuter: Dp = (radius / 2).dp,
    animDuration: Int = 1000,
    animationPlayed: Boolean = false
) {
    var centerCircle by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    val currentUIMode by remember { mutableStateOf(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) }

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) -90f * 12f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        modifier = Modifier
            .size(radiusOuter * 3f)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                val width = size.width
                val height = size.height
                centerCircle = Offset(x = width / 2f, y = height / 2f)

                val totalValue = input.sumOf { it.amount }
                val anglePerValue = (360f / totalValue).toFloat()
                var currentStartAngle = 0f

                input.forEach { chartInput ->
                    val angleToDraw = (chartInput.amount * anglePerValue).toFloat()
                    drawArc(
                        color = if (input.isEmpty()) Color.Gray else
                            chartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = false,
                        style = Stroke(width = innerRadius, cap = StrokeCap.Butt)
                    )
                    currentStartAngle += angleToDraw

                    var rotateAngle = currentStartAngle - angleToDraw / 2f - 90f
                    var factor = 1f
                    if (rotateAngle > 90f) {
                        rotateAngle = (rotateAngle + 180).mod(360f)
                        factor = -0.92f
                    }
                    val percentageValue =
                        (chartInput.amount / totalValue.toFloat() * 100)

                    drawContext.canvas.nativeCanvas.apply {
                        if (percentageValue >= 8.25) {
                            rotate(rotateAngle) {
                                drawText(
                                    "${String.format("%.2f", percentageValue)}%",
                                    centerCircle.x,
                                    centerCircle.y + ((radius * 1.4f) - innerRadius) * factor,
                                    Paint().apply {
                                        textSize = 13.sp.toPx()
                                        textAlign = Paint.Align.CENTER
                                        color = if (currentUIMode == Configuration.UI_MODE_NIGHT_NO)
                                            chartInput.color.toArgb() else Color.White.toArgb()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun PieChart2(
    modifier: Modifier = Modifier,
    input: List<Account>,
    centerText: String = "",
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,

    ) {
    var centerCircle by remember { mutableStateOf(Offset.Zero) }

    var inputList by remember { mutableStateOf(input) }
    var isCenterTapped by remember { mutableStateOf(false) }

    //var currentStartAngle = 0f

    var animationPlayed by remember { mutableStateOf(false) }

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )
    LaunchedEffect(true) {
        animationPlayed = true
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {

                val totalValue = input.sumOf { it.amount }
                val anglePerValue = (360f / totalValue).toFloat()
                var currentStartAngle = 0f
                inputList.forEach { account ->
                    val angleToDraw =
                        (account.amount * anglePerValue).toFloat()
                    drawArc(
                        color = account.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    currentStartAngle += angleToDraw
                }
            }
        }

    }
}


@Composable
fun Chart(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PieChart(
                input = AccountList(),
                centerText = "Net = K250,000.00"
            )
        }
    }
}

@Composable
fun Chart2(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PieChart2(
                modifier = Modifier.size(500.dp),
                input = AccountList(),
                centerText = "Net = K250,000.00"
            )
        }
    }
}

fun AccountList(): List<Account> {
    val accountsSummaryList = listOf(
        AccountsSummary(
            batchName = "",
            flockUniqueID = "",
            totalIncome = 2000.00,
            totalExpenses = 1000.00,
            variance = 1000.00
        ),
        AccountsSummary(
            batchName = "",
            flockUniqueID = "",
            totalIncome = 5000.00,
            totalExpenses = 3500.00,
            variance = 1500.00
        ),
        AccountsSummary(
            batchName = "",
            flockUniqueID = "",
            totalIncome = 17000.00,
            totalExpenses = 12500.00,
            variance = 4500.00
        ),
        AccountsSummary(
            batchName = "",
            flockUniqueID = "",
            totalIncome = 25000.00,
            totalExpenses = 18000.00,
            variance = 7000.00
        )
    )

    return listOf(
        Account(
            color = Color.Green,
            description = "Total Income",
            amount = accountsSummaryList.sumOf { it.totalIncome }
        ),
        Account(
            color = Color.Red,
            description = "Total Expenses",
            amount = accountsSummaryList.sumOf { it.totalExpenses }
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
fun PieChartPreview() {
    NkhukuManagementTheme {
        Chart()
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun PieChartPreview2() {
//    NkhukuManagementTheme {
//        Chart2()
//    }
//}

