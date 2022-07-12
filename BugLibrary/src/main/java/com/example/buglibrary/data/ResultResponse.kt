package com.example.buglibrary.data



data class ResultResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val root: List<T>,// used for about us
)