package com.example.buglibrary.repo

import com.example.buglibrary.data.Poi
import com.example.buglibrary.data.getValueFromDbLiveData
import com.example.buglibrary.data.networkResultLiveData
import com.example.buglibrary.data.resultLiveData
import com.example.buglibrary.db.PoiDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepository @Inject constructor(
    private val dao: PoiDao,
    private val remoteMapDataSource: RemoteMapDataSource
) {

    fun pois(token: String) = resultLiveData(
        databaseQuery = {
            dao.getAllPoi()
        },
        networkCall = {
            remoteMapDataSource.fetchPoi(token)
        },
        saveCallResult = {
            val favPoi = dao.poiIsFavourite().map { it.id }
            dao.insertAll(it.data)
            if (favPoi.isNotEmpty()) {
                dao.updateAllPoi(favPoi)
            }


        }
    )

    fun poiBySubCategory(subCategory: String) = getValueFromDbLiveData(
        databaseQuery = {
            dao.poisBySubCategory(subCategory)
        })

    fun poiByMultipleSubCategory(subCategories: List<String>) = getValueFromDbLiveData(
        databaseQuery = {
            dao.poisByMultipleSubCategory(subCategories)
        })

    fun getAllPoi() = getValueFromDbLiveData(
        databaseQuery = {
            dao.getAllPoi()
        })

    fun favourite(poi: Poi, fcmRegId: String, deviceId: String) = networkResultLiveData {
        if (poi.isFavourite) {
            remoteMapDataSource.addFavourite(poi, fcmRegId, deviceId)
        } else {
            remoteMapDataSource.removeFavourite(poi, deviceId)
        }

    }

    fun allFavouriteData(deviceId: String) = networkResultLiveData {
        remoteMapDataSource.fetchAllFavourite( deviceId)

    }

    suspend fun update(poi: Poi) {
        dao.update(poi)
    }
/*(
        databaseQuery = {
            Log.d("TAG", "addFavourite: inside dab${poi.isFavourite} ")
            MutableLiveData<Result<ResultResponse<Result<Poi>>>>()
        },
        networkCall = {
            Log.d("TAG", "addFavourite:${poi.isFavourite} ")


        },
        saveCallResult = { dao.update(poi) })*/


}
