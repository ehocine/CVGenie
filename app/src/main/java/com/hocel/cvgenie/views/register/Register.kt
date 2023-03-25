package com.hocel.cvgenie.views.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hocel.cvgenie.R
import com.hocel.cvgenie.components.LottieAnim
import com.hocel.cvgenie.navigation.Screens
import com.hocel.cvgenie.ui.theme.BackgroundColor
import com.hocel.cvgenie.ui.theme.ButtonColor
import com.hocel.cvgenie.ui.theme.CircularProgressColor
import com.hocel.cvgenie.utils.Constants.loadingState
import com.hocel.cvgenie.utils.LoadingState
import com.hocel.cvgenie.utils.registerNewUser
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun RegisterPage(
    navController: NavController
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var nameValue by remember { mutableStateOf("") }
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    loadingState = MutableStateFlow(LoadingState.IDLE)
    val state by loadingState.collectAsState()

    var passwordVisibility by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.clickable { focusManager.clearFocus() },
        color = MaterialTheme.colors.BackgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.h4.fontSize
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.register)

                    Spacer(modifier = Modifier.padding(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = nameValue,
                            onValueChange = { nameValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.name),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.name),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.ButtonColor)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = emailValue,
                            onValueChange = { emailValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.ButtonColor)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = passwordValue,
                            onValueChange = { passwordValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.ButtonColor
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.ButtonColor),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }) {
                                    Icon(
                                        if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Password Eye",
                                        tint = MaterialTheme.colors.ButtonColor
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                registerNewUser(
                                    navController = navController,
                                    context = context,
                                    userName = nameValue,
                                    emailAddress = emailValue,
                                    password = passwordValue
                                )
                            },
                            enabled = state != LoadingState.LOADING,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = MaterialTheme.colors.ButtonColor,
                                contentColor = Color.White
                            )
                        ) {
                            if (state == LoadingState.LOADING) {
                                CircularProgressIndicator(color = Color.White)
                            } else {
                                Text(
                                    text = stringResource(R.string.sign_up),
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Row {
                            Text(
                                text = stringResource(R.string.have_an_account_login),
                                fontSize = MaterialTheme.typography.subtitle2.fontSize
                            )
                            Spacer(modifier = Modifier.padding(end = 2.dp))
                            Text(
                                text = "Login",
                                fontSize = MaterialTheme.typography.subtitle2.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.navigate(route = Screens.Login.route) {
                                        // popUpTo = navController.graph.startDestination
                                        launchSingleTop = true
                                    }
                                })
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                    }
                }
            }
        }
    }
}
