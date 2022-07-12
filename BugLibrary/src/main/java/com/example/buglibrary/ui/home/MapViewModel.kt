package com.example.buglibrary.ui.home

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indooratlas.android.sdk.*
import com.example.buglibrary.data.NavDetails
import com.example.buglibrary.data.Poi
import com.example.buglibrary.intelligentprovider.Jarvis
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class MapViewModel @Inject constructor() : ViewModel(),
    IAOrientationListener,
    IALocationListener, IARegion.Listener, Jarvis.RoutingProvider, TextToSpeech.OnInitListener {

    var routePath = MutableLiveData<JSONArray>()
    var turnInfo = MutableLiveData<String>()
    var iaLocationManager: IALocationManager? = null
    var iaLocation = MutableLiveData<IALocation?>()
    private val smartNavigator = Jarvis()
    private lateinit var navDetailJson: JSONObject
    private var previousIndex = -1
    var navDetailList = ArrayList<NavDetails>()
    val heading = MutableLiveData<Double>()
    private var tts: TextToSpeech? = null
    var isSpeaking = true
    var reRoute = MutableLiveData<Boolean>()
    var floorChange = MutableLiveData<Int>()
    var shareUserPosLink = MutableLiveData<String>()
    var shareLocDetail = MutableLiveData<JSONObject>()


    init {


    }

    fun setupIA(context: Context) {
        iaLocationManager = IALocationManager.create(context)

        tts = TextToSpeech(context, this)
        iaLocationManager?.requestLocationUpdates(IALocationRequest.create(), this)
        iaLocationManager?.registerOrientationListener(IAOrientationRequest(5.0, 5.0), this)
        iaLocationManager?.registerRegionListener(this)
        initSmartRoute(this, context)
        setIALocation(25.264181, 55.300155, 0)
        iaLocationManager?.lockIndoors(false)
    }

    override fun onLocationChanged(location: IALocation?) {
        iaLocation.postValue(location!!)
        val iaLatLng = LatLng(location.latitude, location.longitude)
        smartNavigator.onLocationUpdate(iaLatLng, location.floorLevel)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }


    private fun setIALocation(lat: Double, lng: Double, floorNo: Int) {

        iaLocationManager?.setLocation(
            IALocation.Builder()
                .withLatitude(lat)
                .withLongitude(lng)
                .withAccuracy(5.0f)
                .withFloorLevel(floorNo)
                .build()
        )
    }

    private fun initSmartRoute(
        provider: Jarvis.RoutingProvider, context: Context
    ) {
        smartNavigator.initSmartRouter(context, provider)


    }

    override fun onInit(status: Int) {
        when (status) {
            TextToSpeech.SUCCESS -> {
                tts?.language = java.util.Locale.US
            }
        }
    }

    override fun onOrientationChange(p0: Long, p1: DoubleArray?) {
// nothing to do
    }

    override fun onHeadingChanged(p0: Long, heading: Double) {
        this.heading.postValue(heading)
    }


    override fun onReRoute() {
        reRoute.value = true

    }

    override fun currIndex(index: Int) {
        if (previousIndex == index) {
            return
        }
        var turn = ""
        if (::navDetailJson.isInitialized) {
            try {
                val jsonArray = navDetailJson.getJSONArray(index.toString())
                if (jsonArray.length() >= 1) {
                    turn = jsonArray[0].toString()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        previousIndex = index

        if (isSpeaking)
            tts?.speak(turn, TextToSpeech.QUEUE_FLUSH, null, null)
        if (turn.isNotEmpty()) {
            turnInfo.postValue(turn)
        }
    }

    override fun turnByTurnDetail(navDetails: JSONObject?) {
        val navList = ArrayList<NavDetails>()
        navDetailJson = navDetails!!
        navDetails.keys().forEach {
            val jsonArray = navDetails.getJSONArray(it)
            if (jsonArray.length() <= 1) {
                navList.add(NavDetails(jsonArray[0].toString(), ""))
            } else if (jsonArray.length() <= 2) {
                navList.add(NavDetails(jsonArray[0].toString(), jsonArray[1].toString()))
            }
        }
        navDetailList = navList
    }


    override fun route(path: JSONArray?) {
        path?.let {
            routePath.postValue(it)
            smartNavigator.getDistanceAndProjectionOnUpdate()
        }


    }

    fun destinationPoi(poi: Poi) {

        val poiLatLng =
            LatLng(poi.locations?.get(0)?.lat!!, poi.locations?.get(0)?.lng!!)
        smartNavigator.setRouteDestination(
            poiLatLng,
            poi.locations?.get(0)?.levelid!!.toInt()
        )
//        isReroute = true

    }

    override fun onEnterRegion(p0: IARegion?) {

    }

    override fun onExitRegion(region: IARegion?) {
        if (region?.type == IARegion.TYPE_FLOOR_PLAN) {
            // triggered when entering the mapped area of the given floor plan
        } else if (region?.type == IARegion.TYPE_VENUE) {
            // triggered when near a new location

        }
    }

    fun invalidate() {
        smartNavigator.invalidateSmartRouter()
    }

    override fun onCleared() {
        super.onCleared()
//        iaLocation.removeObservers(this)
        invalidate()
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
//        smartNavigator.closeWayFinding()
    }
}