package com.hocel.cvgenie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hocel.cvgenie.navigation.NavGraph
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.CVGenieTheme
import com.hocel.cvgenie.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CVGenieTheme {
                val systemUiController = rememberSystemUiController()
                val systemUIColor = MaterialTheme.colors.BackgroundColor
                navController = rememberAnimatedNavController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = systemUIColor
                    )
                }
                NavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel
                )

            }
        }
    }
}