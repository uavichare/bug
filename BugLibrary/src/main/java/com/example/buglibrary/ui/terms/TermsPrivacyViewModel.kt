package com.example.buglibrary.ui.terms

import androidx.lifecycle.ViewModel
import com.example.buglibrary.data.IpDetail
import com.example.buglibrary.repo.ContentRepository
import javax.inject.Inject

class TermsPrivacyViewModel @Inject constructor(private val contentRepository: ContentRepository) :
    ViewModel() {

    fun getTerms(token: String) = contentRepository.terms(token)
    fun getPrivacyAndAbout(token: String) = contentRepository.aboutUs(token)
/*
  val token =  FirebaseMessaging.getInstance().token
    .addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }
//        val fcmRegToken = task.result?.toString()
//        Log.d("TAG", "fcmToken: $fcmRegToken")


    })
*/
    fun fetchIpDetail() =
        contentRepository.fetchIpDetail()

    fun sendUserDetail(
        ipDetail: IpDetail,
        fcmToken: String,
        language: String,
        deviceId: String
    ) = contentRepository.updatedUserDetail(ipDetail,"", language, deviceId)


}