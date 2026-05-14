package com.esmanureral.neurostage.patients

data class Patient(
    val id: String,
    val fullName: String,
    val age: Int?,
    val gender: String?,
    val createdAtMs: Long?,
)