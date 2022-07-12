package com.example.buglibrary.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Furqan on 21-06-2019.
 */
data class IpDetail(
    val city: String,
    val country: String,
    val countryCode: String,
    val isp: String,
    val lat: Double,
    val lon: Double,
    val org: String,
    val query: String,
    val region: String,
    val regionName: String,
    val status: String,
    val timezone: String,
    val zip: String,
    @field:SerializedName( "as")
    val provider: String


)