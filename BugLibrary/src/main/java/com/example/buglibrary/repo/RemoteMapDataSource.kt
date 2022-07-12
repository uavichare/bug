package com.example.buglibrary.repo

import com.example.buglibrary.api.RequestMap
import com.example.buglibrary.data.BaseDataSource
import com.example.buglibrary.data.Poi
import com.example.buglibrary.helper.AppConstant
import javax.inject.Inject

class RemoteMapDataSource @Inject constructor(private val requestMap: RequestMap) :
    BaseDataSource() {

    suspend fun fetchPoi(token: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["token"] = token
        requestMap.fetchPois(hashMapRequest)
    }


    suspend fun addFavourite(poi: Poi, fcmRegId: String, deviceId: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["device_ids"] = fcmRegId
        hashMapRequest["storedType"] = "poi"
        hashMapRequest["refid"] = poi.id
        hashMapRequest["storedDateTime"] = System.currentTimeMillis().toString()
        hashMapRequest["locationId"] = poi.locations?.get(0)?.locationId!!
        hashMapRequest["uniqueId"] = deviceId
        requestMap.addFavourite(
            "${AppConstant.BASE_URL_PRODUCTION}favouriteroute/addfavourites",
            hashMapRequest
        )
    }

    suspend fun removeFavourite(poi: Poi, deviceId: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["refid"] = poi.id
        hashMapRequest["uniqueId"] = deviceId
        requestMap.removeFavourite(
            "${AppConstant.BASE_URL_PRODUCTION}favouriteroute/removefavourite",
            hashMapRequest
        )
    }

    suspend fun fetchAllFavourite(deviceId: String) = getResult {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["uniqueId"] = deviceId
        requestMap.fetchFav(
            "${AppConstant.BASE_URL_PRODUCTION}favouriteroute/dcaa/getfavourite",
            hashMapRequest
        )
    }

}