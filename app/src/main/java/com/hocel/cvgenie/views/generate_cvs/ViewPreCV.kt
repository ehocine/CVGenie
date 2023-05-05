package com.hocel.cvgenie.views.generate_cvs

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.hocel.cvgenie.components.DisplayLoadingDialog
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.utils.AddOrRemoveAction
import com.hocel.cvgenie.utils.CVDocumentAction
import com.hocel.cvgenie.utils.LoadingState
import com.hocel.cvgenie.utils.toast
import com.hocel.cvgenie.utils.uploadCVDocument
import com.hocel.cvgenie.viewmodels.MainViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ViewPreCV(navController: NavController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val htmlString by remember { mainViewModel.htmlString }
    val webView = WebView(context)
    var pageFinishedLoading by remember { mutableStateOf(false) }
    val state by mainViewModel.creatingCVState.collectAsState()
    var openLoadingDialog by remember { mutableStateOf(false) }

    openLoadingDialog = when (state) {
        LoadingState.LOADING -> true
        else -> false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Preview", color = MaterialTheme.colors.TextColor) },
            actions = {
                Button(
                    onClick = {
                        // Export CV to PDF
                        if (pageFinishedLoading)
                            mainViewModel.saveCVFile(
                                context = context,
                                htmlString = htmlString,
                                onExportFinished = { fileUri ->
                                    mainViewModel.setCreatingCVState(LoadingState.IDLE)
                                    mainViewModel.addOrRemoveCVDocument(
                                        context = context,
                                        action = AddOrRemoveAction.ADD,
                                        CVDocument = mainViewModel.cvInfo.value,
                                        onAddSuccess = {
                                            "Your CV has been created".toast(context, Toast.LENGTH_SHORT)
                                            navController.navigate(Screens.HomeScreen.route) {
                                                popUpTo(navController.graph.findStartDestination().id)
                                                launchSingleTop = true
                                            }
                                            mainViewModel.setCVDocumentAction(action = CVDocumentAction.NONE)
                                            uploadCVDocument(
                                                fileUri = fileUri,
                                                CVDocument = mainViewModel.cvInfo.value
                                            )
                                        },
                                        onRemoveSuccess = {}
                                    )
                                }
                            )
                    },
                    elevation = ButtonDefaults.elevation(0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "EXPORT", color = MaterialTheme.colors.TextColor)
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
                            navController.navigate(Screens.GenerateCVScreen.route)
                        },
                    tint = MaterialTheme.colors.TextColor
                )
            })
    }) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            DisplayLoadingDialog(
                title = "Creating your CV",
                openDialog = openLoadingDialog
            )
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    webView.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        loadDataWithBaseURL(
                            null,
                            htmlString,
                            "text/html",
                            "utf-8",
                            null
                        )
                        this@apply.webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                Log.d("Tag", "Page finished loading")
                                pageFinishedLoading = true
                            }
                        }
                    }
                }
            )
        }
    }
}