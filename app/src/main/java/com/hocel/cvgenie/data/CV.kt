package com.hocel.cvgenie.data

data class CV(
    var firstName: String = "",
    var lastName: String = "",
    var DateOfBirth: Long = System.currentTimeMillis(),
    var placeOfBirth: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var imageUri: String = "",
    var education: List<Education> = listOf(),

    var generatedTime: Long = System.currentTimeMillis()
)
