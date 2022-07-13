package com.example.buglibrary.ui.attraction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.example.buglibrary.data.Poi
import com.example.buglibrary.repo.MapRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShopNDineViewModel @Inject constructor(private val mapRepository: MapRepository) :
    ViewModel() {
/*
    private val token =
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            */
/* // Get new FCM registration token
             val token = task.result*//*


        })
*/

    fun getPoiBySubCategory(category: String) =
        mapRepository.poiBySubCategory(category)

    fun getPoiMultipleSubCategory(categories: List<String>) =
        mapRepository.poiByMultipleSubCategory(categories)


    fun favourite(poi: Poi, deviceId: String) = mapRepository.favourite(poi,"", deviceId)
    fun updatedDb(poi: Poi) {
        viewModelScope.launch {
            mapRepository.update(poi)
        }
    }

}