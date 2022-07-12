package com.example.buglibrary.data

import com.google.gson.annotations.SerializedName


data class NotificationModel(
    @SerializedName("appname")
    val appName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("is_schedule")
    val isSchedule: Boolean,
    val mediaUrl: String,
    val message: String,
    val postedAt: Long,
    val postedBy: String,
    val sendingType: String,
    val title: String
)
