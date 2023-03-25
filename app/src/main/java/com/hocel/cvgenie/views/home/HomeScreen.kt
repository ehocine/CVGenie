package com.hocel.cvgenie.views.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hocel.cvgenie.components.*
import com.hocel.cvgenie.data.CV
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.BottomSheetBackground
import com.hocel.cvgenie.ui.theme.ButtonColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.LoadingState
import com.hocel.cvgenie.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user by mainViewModel.userInfo.collectAsState()
    var imageChosen by remember { mutableStateOf(false) }
    var cVToDelete by remember { mutableStateOf(CV()) }
    val state by mainViewModel.gettingData.collectAsState()

    val deleteModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = deleteModalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colors.BottomSheetBackground,
        sheetContent = {
            DeleteCVDocumentSheetContent(
                context = context,
                CVDocument = cVToDelete,
                scope = scope,
                modalBottomSheetState = deleteModalBottomSheetState,
                mainViewModel = mainViewModel,
                onDeleteYes = {},
                onDeleteCancel = {
                    scope.launch {
                        deleteModalBottomSheetState.hide()
                    }
                }
            )
        }
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = "Create a CV",
                            color = Color.White
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Create,
                            tint = Color.White,
                            contentDescription = null
                        )
                    },
                    backgroundColor = MaterialTheme.colors.ButtonColor,
                    onClick = {
                        navController.navigate(Screens.GenerateCVScreen.route)
                    }
                )
            },
            bottomBar = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                    // shows a traditional banner test ad
//                    AndroidView(
//                        factory = { context ->
//                            AdView(context).apply {
//                                setAdSize(AdSize.BANNER)
//                                adUnitId = context.getString(R.string.ad_id_banner)
//                                loadAd(AdRequest.Builder().build())
//                            }
//                        }
//                    )
                }
            }
        ) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp)
                ) {
                    TopBar(
                        context = context,
                        mainViewModel = mainViewModel,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (state) {
                        LoadingState.LOADING -> LoadingList()
                        LoadingState.ERROR -> ErrorLoadingResults()
                        else -> {
                            if (user.listOfCVS.isEmpty()) {
                                NoResults()
                            } else {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Here are your generated CVs",
                                        textAlign = TextAlign.Start,
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.TextColor,
                                        modifier = Modifier.weight(9f)
                                    )
                                }
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    items(user.listOfCVS) { CVDocument ->
                                        CVDocumentItem(
                                            CVDocument = CVDocument,
                                            onItemClicked = {
                                                mainViewModel.selectCVDocument(it)
                                                navController.navigate(Screens.DetailsScreen.route)
                                            },
                                            enableDeleteAction = true,
                                            deleteScannedText = {
                                                cVToDelete = it
                                                scope.launch {
                                                    deleteModalBottomSheetState.show()
                                                }
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
    }
}