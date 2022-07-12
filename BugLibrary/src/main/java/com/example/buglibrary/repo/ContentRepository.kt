package com.example.buglibrary.repo

import android.util.Log
import com.example.buglibrary.data.IpDetail
import com.example.buglibrary.data.networkResultLiveData
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val remoteMapDataSource: RemoteContentDataSource
) {


    fun aboutUs(token: String) = networkResultLiveData(networkCall = {
        remoteMapDataSource.fetchAbout(token)
    }
    )


    fun fetchIpDetail() = networkResultLiveData(networkCall = {
        remoteMapDataSource.fetchIpDetail()

    })

    fun submitFeedback(
        name: String,
        email: String,
        subject: String,
        message: String,
        type: String
    ) = networkResultLiveData(networkCall = {
        remoteMapDataSource.submitFeedback(name, email, subject, message, type)

    })

    fun fetchFeedbackType(language: String) = networkResultLiveData(networkCall = {
        remoteMapDataSource.fetchFeedbackType(language)

    })

    fun updatedUserDetail(
        ipDetail: IpDetail,
        fcmToken: String,
        language: String,
        deviceId: String
    ) = networkResultLiveData(networkCall = {
        remoteMapDataSource.sendUserDetail(ipDetail, fcmToken, language, deviceId)
    })

    fun loadNotification(
        fcmToken: String,
        language: String,
        deviceId: String
    ) = networkResultLiveData(networkCall = {
        remoteMapDataSource.loadNotification(fcmToken, language, deviceId)
    })

    fun deleteNotification(notificationIds: ArrayList<String>) =
        networkResultLiveData(networkCall = {
            Log.d("TAG", "deleteNotification: n/w $notificationIds ")
            remoteMapDataSource.deleteNotification(notificationIds)
        })

    fun terms(token: String) = networkResultLiveData(networkCall = {
        remoteMapDataSource.fetchTerms(token)
    }
    )

    fun feedback(token: String, ratingBar: String, email: String, description: String) =
        networkResultLiveData(networkCall = {
            remoteMapDataSource.fetchFeedback(token, ratingBar, email, description)
        }
        )

}
