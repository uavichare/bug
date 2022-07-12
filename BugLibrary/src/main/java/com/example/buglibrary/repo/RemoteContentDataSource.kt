package com.example.buglibrary.repo

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.example.buglibrary.api.ApiContent
import com.example.buglibrary.data.BaseDataSource
import com.example.buglibrary.data.IpDetail
import com.example.buglibrary.helper.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.set

class RemoteContentDataSource @Inject constructor(private val requestContent: ApiContent) :
    BaseDataSource() {

    suspend fun fetchAbout(token: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["token"] = token
        requestContent.fetchAboutUs(hashMapRequest)
    }

    suspend fun fetchIpDetail() = getResult {
        requestContent.fetchIpDetail("http://ip-api.com/json")

    }

    suspend fun fetchFeedbackType(language: String) = getResult {
        requestContent.fetchFeedbackSubject("https://dubaiculture.gov.ae/DCALiveSiteWcf/LiveSiteService.svc/GetFeedBackSubjects?language=$language")
    }

    suspend fun submitFeedback(
        name: String,
        email: String,
        subject: String,
        message: String,
        type: String
    ) = getResult {

        val hashMapRequest = HashMap<String, String>()

        hashMapRequest["Name"] = name
        hashMapRequest["Email"] = email
        hashMapRequest["Subject"] = subject
        hashMapRequest["Message"] = message
        hashMapRequest["Type"] = type
        requestContent.fetchFeedbackDcaa(
            "https://dubaiculture.gov.ae/DCALiveSiteWcf/LiveSiteService.svc/SubmitFeedback",
            hashMapRequest
        )
    }

    suspend fun loadNotification(
        fcmToken: String,
        language: String,
        deviceId: String
    ) = getResult {

        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["userId"] = deviceId
        hashMapRequest["deviceId"] = fcmToken
        hashMapRequest["deviceType"] = "Android"
        hashMapRequest["langcode"] = language
        requestContent.fetchNotification(hashMapRequest)
    }

    suspend fun deleteNotification(
        notificationIds: ArrayList<String>
    ) = getResult {

        val requestBody = java.util.HashMap<String, Any>()
        requestBody["appname"] = AppConstant.APP_NAME
        requestBody["nids"] = notificationIds
        val jsonObjectInner = Gson().toJson(requestBody)
        Log.d("TAG", "deleteNotification:ids  $notificationIds")
        Log.d("TAG", "deleteNotification: $jsonObjectInner")
        val jsonObject = JsonParser.parseString(jsonObjectInner.toString()) as JsonObject

        val reqBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        requestContent.deleteNotification(reqBody)
    }

    suspend fun sendUserDetail(
        ipDetail: IpDetail,
        fcmToken: String,
        language: String,
        deviceId: String
    ) = getResult {

        val hashMapRequest = java.util.HashMap<String, String>()
        hashMapRequest["uniqueId"] = deviceId
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["mobile"] = ""
        hashMapRequest["last_name"] = ""
        hashMapRequest["first_name"] = ""
        hashMapRequest["name"] = ""
        hashMapRequest["email"] = ""
        hashMapRequest["device_ids"] = fcmToken
        hashMapRequest["deviceType"] = "Android"
        hashMapRequest["gender"] = ""
        hashMapRequest["DOB"] = ""
        hashMapRequest["other"] = "[]"
        hashMapRequest["deviceDetail"] = Gson().toJson(ipDetail)
        hashMapRequest["userLang"] = language
        requestContent.userDetail(hashMapRequest)

    }

    suspend fun fetchTerms(token: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["token"] = token
        requestContent.termToUse(
            "${AppConstant.BASE_URL_PRODUCTION}contentroute/gettermsforapp",
            hashMapRequest
        )
    }

    suspend fun fetchFeedback(
        token: String,
        ratingBar: String,
        email: String,
        description: String
    ) = getResult {

        val requestBody = HashMap<String, Any>()
        requestBody["user_email"] = email
        requestBody["comment_string"] = description
        requestBody["rating"] = ratingBar

        val request = HashMap<String, Any>()
        request["review"] = requestBody
        request["token"] = token
        val jsonObjectInner = JSONObject(request as Map<*, *>)
        val jsonObject = JsonParser().parse(jsonObjectInner.toString()) as JsonObject
        val reqBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        requestContent.fetchFeedback(
            "${AppConstant.BASE_URL_PRODUCTION}campaignsroute/addreview",
            reqBody
        )

    }

}