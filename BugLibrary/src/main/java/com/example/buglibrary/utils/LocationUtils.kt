package com.example.buglibrary.utils

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationUtils {
    companion object {
/*
        fun convertSphericalToCartesian(latitude: Double, longitude: Double): Vector3 {
            val earthRadius = 6371000.0 //radius in m
            val lat = toRadians(latitude)
            val lon = toRadians(longitude)
            val x = earthRadius * cos(lat) * cos(lon)
            val y = earthRadius * cos(lat) * sin(lon)
            val z = earthRadius * sin(lat)
            return Vector3(x.toFloat(), y.toFloat(), z.toFloat())
        }
*/

        fun bearing(srcLat1: Double, srcLon1: Double, destLat2: Double, destLon2: Double): Double {
            val latitude1 = toRadians(srcLat1)
            val latitude2 = toRadians(destLat2)
            val longDiff = toRadians(destLon2 - srcLon1)
            val y = sin(longDiff) * cos(latitude2)
            val x =
                cos(latitude1) * sin(latitude2) - sin(
                    latitude1
                ) * cos(latitude2) * cos(longDiff)
            return (toDegrees(atan2(y, x)) + 360) % 360
        }

        fun getDistanceFromLatLonInMeters(
            lat1: Double,
            lon1: Double,
            lat2: Double,
            lon2: Double
        ): Double {
            val R = 6371.0 // Radius of the earth in km
            // convert to radians
            val dLat = toRadians(lat2 - lat1)
            val dLon = toRadians(lon2 - lon1)

            val a = sin(dLat / 2) * sin(dLat / 2) + cos(toRadians(lat1)) *
                    cos(toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = R * c // Distance in km

            return d * 1000
        }


    }
}