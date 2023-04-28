package com.hocel.cvgenie.views.generate_cvs

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hocel.cvgenie.components.*
import com.hocel.cvgenie.data.Experience
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.BottomSheetBackground
import com.hocel.cvgenie.ui.theme.ButtonColor
import com.hocel.cvgenie.utils.SheetContentState
import com.hocel.cvgenie.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExperiencePagerScreen(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val listOfExperience = remember { mainViewModel.experienceList }

    val sheetStateContent by mainViewModel.sheetStateContent.collectAsState()

    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colors.BottomSheetBackground,
        sheetContent = {
            when (sheetStateContent) {
                SheetContentState.ADD -> {
                    AddExperienceSheetContent(
                        context = context,
                        mainViewModel = mainViewModel,
                        onAddClicked = {
                            val experience = Experience(
                                employer = mainViewModel.employer.value,
                                position = mainViewModel.position.value,
                                fromYear = mainViewModel.yearOfStart.value,
                                stillWorking = mainViewModel.stillWorking.value,
                                endYear = if (mainViewModel.stillWorking.value) "" else mainViewModel.yearOfEnding.value,
                                comment = mainViewModel.experienceComment.value
                            )
                            mainViewModel.addExperienceItem(experience)
                            scope.launch {
                                modalBottomSheetState.hide()
                            }
                            mainViewModel.resetExperienceFields()
                        }
                    )
                }
                SheetContentState.UPDATE -> {
                    UpdateExperienceSheetContent(
                        context = context,
                        mainViewModel = mainViewModel,
                        onSaveClicked = {
                            mainViewModel.updateExperienceItem(
                                index = listOfExperience.indexOf(mainViewModel.selectedExperienceItem.value),
                                experience = it
                            )
                            scope.launch {
                                modalBottomSheetState.hide()
                            }
                            mainViewModel.resetExperienceFields()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = "Add new",
                            color = Color.White
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = Color.White,
                            contentDescription = null
                        )
                    },
                    backgroundColor = MaterialTheme.colors.ButtonColor,
                    onClick = {
                        mainViewModel.setSheetStateContent(SheetContentState.ADD)
                        scope.launch {
                            modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                )
            }
        ) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                when {
                    listOfExperience.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Title(title = "Nothing to show here yet")
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colors.BackgroundColor)
                        ) {
                            items(listOfExperience) { experience ->
                                ExperienceInfoItem(
                                    experienceInfo = experience,
                                    onItemClicked = {
                                        mainViewModel.setSelectedExperienceItem(it)
                                        mainViewModel.setSheetStateContent(SheetContentState.UPDATE)
                                        scope.launch {
                                            modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                        }
                                    },
                                    enableDeleteAction = true,
                                    deleteEducationalInfo = {
                                        mainViewModel.removeExperienceItem(it)
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