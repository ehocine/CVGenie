package com.hocel.cvgenie.data

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var listOfCVs: List<CV> = listOf(),
)
