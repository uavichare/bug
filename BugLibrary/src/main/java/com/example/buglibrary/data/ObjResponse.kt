package com.example.buglibrary.data

/**
 * Created by Furqan on 23-07-2018.
 */
data class ObjResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val url: String,
    val message: String,
    val code: Int
)