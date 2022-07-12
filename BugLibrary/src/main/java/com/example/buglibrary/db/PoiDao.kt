package com.example.buglibrary.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.buglibrary.data.Poi

@Dao
interface PoiDao {
    @Query("SELECT * FROM Poi")
    fun getAllPoi(): LiveData<List<Poi>>

    @Query("UPDATE Poi Set isFavourite=1 where id in (:id)")
    fun updateAllPoi(id: List<String>)

    @Query("select * from Poi where id = :id")
    fun poiById(id: String): Poi

    @Query("select * from Poi where isFavourite= 1")
    fun poiIsFavourite(): List<Poi>

    @Query("select * from Poi where subcategory= :subCategory collate nocase")
    fun poisBySubCategory(subCategory: String): LiveData<List<Poi>>

    @Query("select * from Poi where subcategory in (:subCategories) collate nocase")
    fun poisByMultipleSubCategory(subCategories: List<String>): LiveData<List<Poi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(poi: List<Poi>)

    @Update
    suspend fun update(poi: Poi)

    @Delete
    suspend fun deletePoi(poi: Poi)
}
