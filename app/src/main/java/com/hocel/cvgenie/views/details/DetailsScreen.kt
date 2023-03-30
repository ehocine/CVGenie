package com.hocel.cvgenie.views.details

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.hocel.cvgenie.components.CVDetailsDropMenu
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.CVDocumentAction
import com.hocel.cvgenie.utils.saveFileLocally
import com.hocel.cvgenie.utils.showInterstitial
import com.hocel.cvgenie.viewmodels.MainViewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailsScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(mainViewModel.selectedCVDocument.value.cvUrl),
        isZoomEnable = true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", color = MaterialTheme.colors.TextColor) },
                actions = {
                    CVDetailsDropMenu(
                        onEditClicked = {
                            mainViewModel.resetCVFields()
                            mainViewModel.setCVDocumentAction(action = CVDocumentAction.EDIT)
                            mainViewModel.setCVDocumentToEdit(mainViewModel.selectedCVDocument.value)
                            navController.navigate(Screens.GenerateCVScreen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        onSaveClicked = {
                            saveFileLocally(
                                context = context,
                                CVDocument = mainViewModel.selectedCVDocument.value
                            )
                        }
                    )
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
                                navController.navigateUp()
                                mainViewModel.setCVDocumentAction(action = CVDocumentAction.NONE)
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        }
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
            showInterstitial(context)
            VerticalPDFReader(
                state = pdfState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .background(color = MaterialTheme.colors.BackgroundColor)
            )
            when (pdfState.isLoaded) {
                false -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                    }
                }
                else -> Unit
            }
        }
    }
}