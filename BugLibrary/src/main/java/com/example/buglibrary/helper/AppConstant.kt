package com.example.buglibrary.helper


object AppConstant {
    const val NOTIFICATION_TYPE = "notification_type"
    const val ON_BOARDING: String = "on_boarding"
    const val UNIQUE_USER_ID = "device_id"
    const val BASE_URL = "https://development.wisefly.in/"
    //const val BASE_URL_PRODUCTION = "http://dcaa.wisefly.in/"

    const val BASE_URL_PRODUCTION = "https://wayfinding.dubaiculture.gov.ae/"
    val INDOOR_ATlAS_APIKEY = "indoor_atlas_key"
    val INDOOR_ATlAS_SECRETKEY = "indoor_atlas_secret_key"
    val MAPBOX_TOKEN = "mapbox_token"


    const val PINNED_BASE_URL_DEV: String = "development.wisefly.in"
    const val APP_NAME = "dcaa"
    const val IS_SPEAKING = "voice"

    const val LANGUAGE_KEY = "language_key"


    // map location interval
    const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    // geofence
    private val PACKAGE_NAME: String = "com.insuide.alfahidihistoricalwayfinding.service.Geofence"

    val GEOFENCES_ADDED_KEY = "$PACKAGE_NAME.GEOFENCES_ADDED_KEY"

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private const val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12

    /**
     * For this sample, geofences expire after twelve hours.
     */
    const val GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    const val GEOFENCE_RADIUS_IN_METERS = 150f//1609f // 1 mile, 1.6 km
    const val GEO_NOTI = "geo_noti"


}