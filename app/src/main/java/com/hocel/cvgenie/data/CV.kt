package com.hocel.cvgenie.data

data class CV(
    var firstName: String = "",
    var lastName: String = "",
    var jobTitle: String = "",
    var dateOfBirth: String = "",
    var placeOfBirth: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var skills: String = "",
    var languages: String = "",
    var imageUri: String = "",
    var education: List<Education> = listOf(),
    var experience: List<Experience> = listOf(),
    var generatedTime: Long = System.currentTimeMillis(),
    var cvUrl: String = ""
)
