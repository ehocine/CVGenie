package com.hocel.cvgenie.views.generate_cvs

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hocel.cvgenie.R
import com.hocel.cvgenie.components.NavigateUpSheetContent
import com.hocel.cvgenie.data.CV
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.BottomSheetBackground
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.*
import com.hocel.cvgenie.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun GenerateCVScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val pages = listOf(
        stringResource(R.string.personal_info),
        stringResource(R.string.education),
        stringResource(R.string.experience)
    )

    val action by mainViewModel.cvDocumentAction
    var title = "Generate CV"

    when (action) {
        CVDocumentAction.GENERATE -> {
            title = "Generate CV"
        }

        CVDocumentAction.EDIT -> {
            title = "Edit CV"
        }

        else -> Unit
    }

    val pagerState = rememberPagerState()

    BackHandler {
        when {
            pagerState.currentPage != 0 -> scope.launch {
                pagerState.animateScrollToPage(0)
            }

            else -> {
                scope.launch {
                    modalBottomSheetState.show()
                }
            }
        }
    }
    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colors.BottomSheetBackground,
        sheetContent = {
            NavigateUpSheetContent(
                onYesClicked = {
                    scope.launch {
                        modalBottomSheetState.hide()
                        navController.navigate(Screens.HomeScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                        mainViewModel.setCVDocumentAction(action = CVDocumentAction.NONE)
                    }
                }, onCancel = {
                    scope.launch {
                        modalBottomSheetState.hide()
                    }
                })
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title, color = MaterialTheme.colors.TextColor) },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screens.ViewPreCV.route)
                            val cv = CV(
                                firstName = mainViewModel.firstName.value,
                                lastName = mainViewModel.lastName.value,
                                jobTitle = mainViewModel.jobTitle.value,
                                dateOfBirth = mainViewModel.dateOfBirth.value,
                                placeOfBirth = mainViewModel.placeOfBirth.value,
                                email = mainViewModel.emailAddress.value,
                                phoneNumber = mainViewModel.phoneNumber.value,
                                skills = mainViewModel.skills.value,
                                languages = mainViewModel.languages.value,
                                address = mainViewModel.personalAddress.value,
                                imageUri = mainViewModel.localImageUri.value.toString(),
                                education = mainViewModel.educationList,
                                experience = mainViewModel.experienceList,
                                generatedTime = System.currentTimeMillis()
                            )
                            if (cv.imageUri.isNotEmpty()
                                && cv.firstName.isNotEmpty()
                                && cv.lastName.isNotEmpty()
                                && cv.jobTitle.isNotEmpty()
                                && cv.dateOfBirth.isNotEmpty()
                                && cv.placeOfBirth.isNotEmpty()
                                && cv.address.isNotEmpty()
                                && cv.email.isNotEmpty()
                                && cv.phoneNumber.isNotEmpty()
                            ) {
                                mainViewModel.setCVInfo(cv)
                                mainViewModel.generateV2()
                                navController.navigate(Screens.ViewPreCV.route)
                            } else {
                                "Must fill the required fields".toast(
                                    context,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "",
                                tint = MaterialTheme.colors.TextColor
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colors.BackgroundColor,
                    contentColor = MaterialTheme.colors.TextColor,
                    elevation = 0.dp,
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    scope.launch {
                                        modalBottomSheetState.show()
                                    }
                                },
                            tint = MaterialTheme.colors.TextColor
                        )
                    }
                )
            }
        ) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.BackgroundColor)
                ) {
                    showInterstitial(context)
                    TabRow(
                        backgroundColor = MaterialTheme.colors.BackgroundColor,
                        selectedTabIndex = pagerState.currentPage,
                        divider = {},
                        indicator = {
                            TabRowDefaults.Indicator(
                                height = 2.4f.dp,
                                color = MaterialTheme.colors.TextColor,
                                modifier = Modifier
                                    .pagerTabIndicatorOffset(pagerState, it)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    ) {
                        pages.forEachIndexed { index, label ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                selectedContentColor = Color.Transparent,
                                text = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        color = MaterialTheme.colors.TextColor,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontWeight = FontWeight.W600
                                    )
                                },
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        count = pages.size,
                        modifier = Modifier
                            .padding(top = 16.dp)
                    ) { page ->
                        when (page) {
                            0 -> PersonalInfoPagerScreen(mainViewModel = mainViewModel)
                            1 -> EducationPagerScreen(mainViewModel = mainViewModel)
                            2 -> ExperiencePagerScreen(mainViewModel = mainViewModel)
                        }
                    }
                }
            }

        }
    }
}