package com.hocel.cvgenie.views.generate_cvs

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hocel.cvgenie.R
import com.hocel.cvgenie.components.NavigateUpSheetContent
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.BottomSheetBackground
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun GenerateCVScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val pages = listOf(
        stringResource(R.string.personal_info),
        stringResource(R.string.education),
        stringResource(R.string.experience)
    )

    //To reset all fields to initial values
    mainViewModel.resetCVFields()

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
                        navController.navigateUp()
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
                    title = { Text("Generate a CV", color = MaterialTheme.colors.TextColor) },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                //TODO: Generate a CV
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
                                .size(24.dp, 24.dp)
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

            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    TabRow(
                        backgroundColor = MaterialTheme.colors.background,
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