package com.example.buglibrary.ui.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.buglibrary.data.NotificationModel
import com.example.buglibrary.data.Result
import com.example.buglibrary.data.ResultResponse
import com.example.buglibrary.repo.ContentRepository
import java.util.*
import javax.inject.Inject

class NotificationViewModel @Inject constructor(private val contentRepository: ContentRepository) :
    ViewModel() {

    fun fetchNotification(
        fcmToken: String,
        language: String,
        deviceId: String
    ) = contentRepository.loadNotification(fcmToken, language, deviceId)

    fun deleteNotification(notificationIds: ArrayList<String>): LiveData<Result<ResultResponse<NotificationModel>>> {
        Log.d("TAG", "deleteNotification: view model $notificationIds")
        return contentRepository.deleteNotification(notificationIds)
    }

}