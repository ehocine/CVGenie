package com.hocel.cvgenie.data

data class Experience(
    val employer : String = "",
    val position : String = "",
    val fromYear : String = "",
    val endYear : String = "",
    val stillWorking: Boolean = false,
    val comment: String = ""
)
