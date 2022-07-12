package com.example.buglibrary.ui.newcontactus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buglibrary.data.ObjResponse
import com.example.buglibrary.data.Result
import com.example.buglibrary.repo.ContentRepository
import javax.inject.Inject

class NewContactUsViewModel @Inject constructor(private val contentRepository: ContentRepository) :
    ViewModel() {

    val contactUsList = MutableLiveData<List<NewContactUsModel>>().apply {
        val arrayList = ArrayList<NewContactUsModel>()

        arrayList.add(NewContactUsModel(NewContactUsComponent.MAP_SECTION))
        arrayList.add(NewContactUsModel(NewContactUsComponent.REACH_US))
        arrayList.add(NewContactUsModel(NewContactUsComponent.WORKING_HOURS))
        arrayList.add(NewContactUsModel(NewContactUsComponent.SUGGESSTIONS_AND_COMPLAINS))
        arrayList.add(NewContactUsModel(NewContactUsComponent.SOCIAL_MEDIA))

        value = arrayList
    }

    fun getsendFeedback(
        token: String,
        ratingbar: String,
        emailText: String,
        DescriptionText: String
    ): LiveData<Result<ObjResponse<Any>>> =
        contentRepository.feedback(token, ratingbar, emailText, DescriptionText)

    fun submitFeedback(
        name: String,
        email: String,
        subject: String,
        message: String,
        type: String
    ) = contentRepository.submitFeedback(name, email, subject, message, type)

    fun fetchFeedbackType(language: String) = contentRepository.fetchFeedbackType(language)

}
