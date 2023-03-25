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
import com.hocel.cvgenie.components.AddEducationSheetContent
import com.hocel.cvgenie.components.EducationInfoItem
import com.hocel.cvgenie.components.Title
import com.hocel.cvgenie.components.UpdateEducationSheetContent
import com.hocel.cvgenie.data.Education
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.BottomSheetBackground
import com.hocel.cvgenie.ui.theme.ButtonColor
import com.hocel.cvgenie.utils.SheetContentState
import com.hocel.cvgenie.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EducationPagerScreen(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val listOfEducation = remember { mainViewModel.educationList }

    val sheetStateContent by mainViewModel.sheetStateContent.collectAsState()

    var selectedEducationItem by remember { mutableStateOf(Education()) }

    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colors.BottomSheetBackground,
        sheetContent = {
            when (sheetStateContent) {
                SheetContentState.ADD -> {
                    AddEducationSheetContent(
                        context = context,
                        mainViewModel = mainViewModel,
                        onAddClicked = {
                            val education = Education(
                                school = mainViewModel.school.value,
                                field = mainViewModel.field.value,
                                diploma = mainViewModel.diploma.value,
                                yearOfDiploma = mainViewModel.yearOfDiploma.value
                            )
                            mainViewModel.addEducationItem(education)
                            scope.launch {
                                modalBottomSheetState.hide()
                            }
                            mainViewModel.resetEducationFields()
                        }
                    )
                }
                SheetContentState.UPDATE -> {
                    UpdateEducationSheetContent(
                        educationItem = selectedEducationItem,
                        context = context
                    ) {
                        mainViewModel.updateEducationItem(
                            index = listOfEducation.indexOf(
                                selectedEducationItem
                            ), education = it
                        )
                        scope.launch {
                            modalBottomSheetState.hide()
                        }
                    }
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
            when {
                listOfEducation.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Title(title = "Nothing to show here yet")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colors.BackgroundColor)
                    ) {
                        items(listOfEducation) { education ->
                            EducationInfoItem(
                                educationInfo = education,
                                onItemClicked = {
                                    selectedEducationItem = it
                                    mainViewModel.setSheetStateContent(SheetContentState.UPDATE)
                                    scope.launch {
                                        modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                    }
                                },
                                enableDeleteAction = true,
                                deleteEducationalInfo = {
                                    mainViewModel.removeEducationItem(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}