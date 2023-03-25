package com.hocel.cvgenie.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hocel.cvgenie.ui.theme.TextColor
import com.hocel.cvgenie.viewmodels.MainViewModel

@Composable
fun TopBar(
    context: Context,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo(context = context)
    }

    val user by mainViewModel.userInfo.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hey ${user.name}",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.TextColor
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
          SignOutDropMenu {
              mainViewModel.signOut(context = context, navController = navController)
          }
        }
    }
}