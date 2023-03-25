package com.hocel.cvgenie.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

object Constants {
    const val TIMEOUT_IN_MILLIS = 10000L

    var loadingState = MutableStateFlow(LoadingState.IDLE)

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    const val FIRESTORE_USERS_DATABASE = "users"

    const val LIST_OF_CVs = "listOfCVs"
}