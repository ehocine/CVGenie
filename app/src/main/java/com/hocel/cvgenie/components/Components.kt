package com.hocel.cvgenie.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hocel.cvgenie.data.CV
import com.hocel.cvgenie.data.Education
import com.hocel.cvgenie.data.Experience
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.ButtonColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.AddOrRemoveAction
import com.hocel.cvgenie.utils.deleteCVDocumentFromStorage
import com.hocel.cvgenie.utils.toast
import com.hocel.cvgenie.viewmodels.MainViewModel
import com.maxkeppeker.sheets.core.models.base.SheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun TransparentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indication: Indication = rememberRipple(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        elevation = 0.dp,
        shape = shape,
        color = Color.Transparent,
        contentColor = Color.Transparent,
        border = null,
        modifier = modifier
            .then(
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        onClick = onClick
                    )
            ),
    ) {
        CompositionLocalProvider(LocalContentAlpha provides 1f) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigateUpSheetContent(
    onYesClicked: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Changes will be lost, still want to proceed?",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            color = MaterialTheme.colors.TextColor
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onYesClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Yes",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
        Divider(
            color = Color.Black.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onCancel()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteCVDocumentSheetContent(
    context: Context,
    CVDocument: CV,
    scope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    mainViewModel: MainViewModel,
    onDeleteYes: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Are you sure you want to delete?",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            color = MaterialTheme.colors.TextColor
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                mainViewModel.addOrRemoveScannedText(
                    context = context,
                    action = AddOrRemoveAction.REMOVE,
                    CVDocument = CVDocument,
                    onAddSuccess = {},
                    onRemoveSuccess = {
                        deleteCVDocumentFromStorage(CVDocument)
                        onDeleteYes()
                        scope.launch {
                            modalBottomSheetState.hide()
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Yes",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
        Divider(
            color = Color.Black.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onDeleteCancel()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
    }
}


@Composable
fun AddEducationSheetContent(
    context: Context,
    mainViewModel: MainViewModel,
    onAddClicked: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(title = "Add your education information")
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoCVItem(
                title = "School",
                value = mainViewModel.school.value,
                label = "School",
                placeholder = "School",
                onValueChanged = {
                    mainViewModel.setSchool(it)
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Field",
                value = mainViewModel.field.value,
                label = "Field",
                placeholder = "Field",
                onValueChanged = {
                    mainViewModel.setField(it)
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Diploma",
                value = mainViewModel.diploma.value,
                label = "Diploma",
                placeholder = "Diploma",
                onValueChanged = {
                    mainViewModel.setDiploma(it)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Year",
                value = mainViewModel.yearOfDiploma.value,
                label = "Year",
                placeholder = "Year",
                keyboardType = KeyboardType.Number,
                onValueChanged = {
                    mainViewModel.setYearOfDiploma(it)
                }
            )
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.ButtonColor,
                contentColor = Color.White
            ),
            onClick = {
                if (
                    mainViewModel.school.value.isNotEmpty()
                    && mainViewModel.field.value.isNotEmpty()
                    && mainViewModel.diploma.value.isNotEmpty()
                    && mainViewModel.yearOfDiploma.value.isNotEmpty()
                ) {
                    onAddClicked()
                } else {
                    "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                }
            }) {
            Text(
                text = "Add"
            )
        }
    }
}

@Composable
fun UpdateEducationSheetContent(
    educationItem: Education,
    context: Context,
    onSaveClicked: (newEducationItem: Education) -> Unit
) {
    var school by remember { mutableStateOf(educationItem.school) }
    var field by remember { mutableStateOf(educationItem.field) }
    var diploma by remember { mutableStateOf(educationItem.diploma) }
    var year by remember { mutableStateOf(educationItem.yearOfDiploma) }
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(title = "Update your education information")
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoCVItem(
                title = "School",
                value = school,
                label = "School",
                placeholder = "School",
                onValueChanged = {
                    school = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Field",
                value = field,
                label = "Field",
                placeholder = "Field",
                onValueChanged = {
                    field = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Diploma",
                value = diploma,
                label = "Diploma",
                placeholder = "Diploma",
                onValueChanged = {
                    diploma = it
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Year",
                value = year,
                label = "Year",
                placeholder = "Year",
                keyboardType = KeyboardType.Number,
                onValueChanged = {
                    year = it
                }
            )
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.ButtonColor,
                contentColor = Color.White
            ),
            onClick = {
                if (
                    school.isNotEmpty()
                    && field.isNotEmpty()
                    && diploma.isNotEmpty()
                    && year.isNotEmpty()
                ) {
                    val newEducationItem = Education(
                        school, field, diploma, year
                    )
                    onSaveClicked(newEducationItem)
                } else {
                    "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                }
            }) {
            Text(
                text = "Save"
            )
        }
    }
}


@Composable
fun AddExperienceSheetContent(
    context: Context,
    mainViewModel: MainViewModel,
    onAddClicked: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(title = "Add your experience information")
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoCVItem(
                title = "Employer",
                value = mainViewModel.employer.value,
                label = "Employer",
                placeholder = "Employer",
                onValueChanged = {
                    mainViewModel.setEmployer(it)
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Position",
                value = mainViewModel.position.value,
                label = "Position",
                placeholder = "Position",
                onValueChanged = {
                    mainViewModel.setPosition(it)
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Year of starting",
                value = mainViewModel.yearOfStart.value,
                label = "Year of starting",
                placeholder = "Year of starting",
                keyboardType = KeyboardType.Number,
                onValueChanged = {
                    mainViewModel.setYearOfStarting(it)
                }
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mainViewModel.stillWorking.value,
                    onCheckedChange = {
                        mainViewModel.setStillWorking(it)
                    })
                Text(text = "Still working here")
            }
            if (!mainViewModel.stillWorking.value) {
                InfoCVItem(
                    title = "Year of ending",
                    value = mainViewModel.yearOfEnding.value,
                    label = "Year of ending",
                    placeholder = "Year of ending",
                    keyboardType = KeyboardType.Number,
                    onValueChanged = {
                        mainViewModel.setYearOfEnding(it)
                    }
                )
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.ButtonColor,
                contentColor = Color.White
            ),
            onClick = {
                if (
                    mainViewModel.employer.value.isNotEmpty()
                    && mainViewModel.position.value.isNotEmpty()
                    && mainViewModel.yearOfStart.value.isNotEmpty()
                ) {
                    if (!mainViewModel.stillWorking.value) {
                        if (mainViewModel.yearOfEnding.value.isNotEmpty()) {
                            onAddClicked()
                        } else {
                            "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                        }
                    } else {
                        onAddClicked()
                    }
                } else {
                    "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                }
            }) {
            Text(
                text = "Add"
            )
        }
    }
}

@Composable
fun UpdateExperienceSheetContent(
    experienceItem: Experience,
    context: Context,
    onSaveClicked: (newExperienceItem: Experience) -> Unit
) {
    var employer by remember { mutableStateOf(experienceItem.employer) }
    var position by remember { mutableStateOf(experienceItem.position) }
    var yearOfStart by remember { mutableStateOf(experienceItem.fromYear) }
    var stillWorking by remember { mutableStateOf(experienceItem.stillWorking) }
    var yearOfEnd by remember { mutableStateOf(experienceItem.endYear) }
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(title = "Update your experience information")
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoCVItem(
                title = "Employer",
                value = employer,
                label = "Employer",
                placeholder = "Employer",
                onValueChanged = {
                    employer = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Position",
                value = position,
                label = "Position",
                placeholder = "Position",
                onValueChanged = {
                    position = it
                })
            Spacer(modifier = Modifier.height(16.dp))
            InfoCVItem(
                title = "Year of starting",
                value = yearOfStart,
                label = "Year of starting",
                placeholder = "Year of starting",
                keyboardType = KeyboardType.Number,
                onValueChanged = {
                    yearOfStart = it
                }
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = stillWorking,
                    onCheckedChange = {
                        stillWorking = it
                    })
                Text(text = "Still working here")
            }
            if (!stillWorking) {
                InfoCVItem(
                    title = "Year of ending",
                    value = yearOfEnd,
                    label = "Year of ending",
                    placeholder = "Year of ending",
                    keyboardType = KeyboardType.Number,
                    onValueChanged = {
                        yearOfEnd = it
                    }
                )
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.ButtonColor,
                contentColor = Color.White
            ),
            onClick = {
                val newExperienceItem =
                    Experience(employer, position, yearOfStart, yearOfEnd, stillWorking)
                if (
                    employer.isNotEmpty()
                    && position.isNotEmpty()
                    && yearOfStart.isNotEmpty()
                ) {
                    if (!stillWorking) {
                        if (yearOfEnd.isNotEmpty()) {
                            onSaveClicked(newExperienceItem)
                        } else {
                            "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                        }
                    } else {
                        onSaveClicked(newExperienceItem)
                    }
                } else {
                    "Fields cannot be empty".toast(context, Toast.LENGTH_SHORT)
                }
            }) {
            Text(
                text = "Save"
            )
        }
    }
}

@Composable
fun DatePicker(
    state: SheetState,
    onDateSelected: (date: LocalDate) -> Unit
) {
    CalendarDialog(
        state = state,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            onDateSelected(date)
        })
}

@Composable
fun InfoCVItem(
    title: String,
    value: String,
    label: String,
    placeholder: String,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable() (() -> Unit)? = null,
    onValueChanged: (value: String) -> Unit
) {
    Title(title = title)
    Spacer(modifier = Modifier.height(10.dp))
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        label = {
            Text(
                text = label,
                color = MaterialTheme.colors.ButtonColor
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colors.ButtonColor
            )
        },
        singleLine = singleLine,
        maxLines = maxLines,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp, 16.dp, 0.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.ButtonColor
        ),
        trailingIcon = trailingIcon

    )
}

@Composable
fun SignOutDropMenu(onSignOutClicked: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Menu",
            tint = MaterialTheme.colors.TextColor
        )
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colors.BackgroundColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                expanded = false
                onSignOutClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign out",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = "Sign out",
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }
        }
    }
}
