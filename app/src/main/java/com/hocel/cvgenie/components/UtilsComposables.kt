package com.hocel.cvgenie.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.R


@Composable
fun LoadingList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
    }
}


@Composable
fun ErrorLoadingResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
        Text(
            text = "Error loading data",
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun NoResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
        Text(
            text = "No entries",
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun DisplayLoadingDialog(
    title: String,
    openDialog: Boolean
) {
    if (openDialog) {
        AlertDialog(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Title(title = title)
                }
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                }
            },
            buttons = {},
            onDismissRequest = {}
        )
    }
}