package com.example.buglibrary.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.indooratlas.android.sdk.IALocation
import com.indooratlas.android.sdk.IAWayfindingRequest
import com.example.buglibrary.AlFahidiWayFindingApp
import com.example.buglibrary.MainActivity
import com.example.buglibrary.R
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.FragmentHomeBinding

import com.example.buglibrary.di.Injectable
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.get
import com.example.buglibrary.helper.PreferenceHelper.set
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.utils.CommonUtils
import com.example.buglibrary.data.Result

import com.example.buglibrary.ui.home.ar.ArActivity
import com.example.buglibrary.utils.GeoCentricUtil
import com.example.buglibrary.utils.SessionUtils
import com.example.buglibrary.utils.ext.injectViewModel
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.BubbleLayout
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


internal const val REQUEST_CHECK_SETTINGS = 1

class HomeFragment : Fragment(), Injectable,
    MapboxMap.OnMapClickListener, View.OnClickListener {


    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private lateinit var homeViewModel: HomeViewModel


    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewMap: HashMap<String, View> = HashMap()
    private var featureCollection: FeatureCollection? = null
    private var mapboxMap: MapboxMap? = null
    var poiList: List<Poi>? = null
    private var poi: Poi? = null
    private var etaTotalTime: String = ""
    private var currentLocation: Location? = null
    private lateinit var app: AlFahidiWayFindingApp
    private lateinit var mainActivity: MainActivity
    private var isHideToolbar = false
    private var poiFeature: Feature? = null
    @ExperimentalCoroutinesApi
    private var searchChannel = BroadcastChannel<IALocation>(1)
    private var totalDistance = 0.0

    private val listOfLatLng = ArrayList<LatLng>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            app = activity?.application as AlFahidiWayFindingApp
            homeViewModel = injectViewModel(viewModelProvider)
            mapViewModel = injectViewModel(viewModelProvider)

            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            binding.mapView.onCreate(savedInstanceState)
//            poi = HomeFragmentArgs.fromBundle(requireArguments()).drawRoute
            mainActivity = activity as MainActivity
            binding.mapView.getMapAsync {
                setMapBox(it)
            }
            binding.btnArView.setOnClickListener {
                if (SessionUtils.checkIsSupportedDeviceOrFinish(requireActivity())) {
/*
                    val direction = HomeFragmentDirections.actionNavigationFragment(poi)
                    findNavController().navigate(direction)
*/
                } else {
                    Snackbar.make(it, "Your device doesn't support AR", Snackbar.LENGTH_SHORT)
                        .show()
                }

            }
//            val rotation =
//                if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) 0f else 180f

//            binding.layoutNavigation.imgBack.rotation = rotation
        }
        app.mapViewModel = mapViewModel
//        app.poi?.let {
        mapViewModel.routePath.observe(viewLifecycleOwner) { jsonArray ->
            if (jsonArray != null) {

                drawRoute(jsonArray)
                mapViewModel.routePath.value = null
            } else {
//                stopNavigation()
            }

        }
//        }


        return binding.root
    }


    fun isNavigationLayoutVisible() =
        if (_binding != null) binding.layoutNavigation.layoutNavigation.isShown else false


    private fun showEnableLocationSetting() {
        activity?.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(it)
                .checkLocationSettings(builder.build())

            task.addOnSuccessListener { response ->
//                val states = response.locationSettingsStates
                mainActivity.addGeofence()
//                if (states.isLocationPresent) {
//                    //Do something
//                }
            }
            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        // Handle result in onActivityResult()
                        e.startResolutionForResult(it, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }

    }

    private var flag = false
    @ObsoleteCoroutinesApi
    private fun setMapBox(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.addOnMapClickListener(this)
        /* val pair1 = Pair(
             R.drawable.poi_food.toString(),
             ResourcesCompat.getDrawable(resources, R.drawable.poi_food, null)!!
         )
         val pair2 = Pair(
             R.drawable.post_office.toString(),
             shapeDrawable(R.drawable.poi_services, R.drawable.post_office)
         )*/
        val ar = ArrayList<Pair<String, Bitmap>>()
        val arDrawable = ArrayList<Pair<String, Drawable>>()

        homeViewModel.poiImageMap().forEach {
            val pair2 = Pair(
                "end_${it}",
                shapeDrawable(it)
            )
            arDrawable.add(pair2)
            ar.add(Pair(it.toString(), BitmapFactory.decodeResource(resources, it)))
        }



        mapboxMap.setStyle(
            Style.Builder()
                .fromUri(getString(R.string.mapbox_style_url))
                .withDrawableImages(*arDrawable.toTypedArray())
                .withBitmapImages(*ar.toTypedArray())
            /* .withImage(
                 R.drawable.poi_food.toString(),
                 ResourcesCompat.getDrawable(resources, R.drawable.poi_food, null)!!
             )*/
        ) {


            //enableLocationComponent()


            homeViewModel.mapPoi(getString(R.string.app_token))
                .observe(viewLifecycleOwner) { result ->
                    when (result.status) {
                        Result.Status.SUCCESS -> {
//                           mapViewModel.setupIA(requireContext())
                            if (result.data.isNullOrEmpty().not()) {
                                poiList = result.data


                                //poi info window
                                if (!flag) {
                                    flag = true
                                    lifecycleScope.launch {

                                        val features = homeViewModel.poiFeatureList(result.data!!)
                                        featureCollection = FeatureCollection.fromFeatures(features)
                                        val resultInfo = generateInfoWindow(featureCollection!!)

                                        setImageGenResults(resultInfo)

                                        homeViewModel.addSymbolLayer(
                                            mapboxMap, "source_floor_1",
                                            "layer_floor_1", features,
                                            requireContext()
                                        )
                                        homeViewModel.addSymbolSelectedLayer(
                                            mapboxMap, "animate_source",
                                            "animate_layer",
                                            requireContext()
                                        )

                                        homeViewModel.setupCalloutLayer(mapboxMap, it)
                                        animateNearPois()
                                    }
                                }

//                                homeViewModel.startIconSpinningAnimation(mapboxMap)
                            }

                        }
                        Result.Status.ERROR -> {

                        }
                        Result.Status.LOADING -> {

                        }

                    }
                }


            lifecycleScope.launch {
                delay(1500)
                animateCamera(LatLng(25.264181, 55.300155), 18.0)
            }

        }
    }

    @ObsoleteCoroutinesApi
    private fun animateNearPois() {
        val selectedMarkerSymbolLayer = mapboxMap?.style?.getLayer("animate_layer") as SymbolLayer
        lifecycleScope.launch {
            searchChannel.consumeEach {
                delay(3000)

                val features = ArrayList<Feature>()
                poiList?.forEach { p ->
                    val loc = p.locations
                    if (loc.isNullOrEmpty().not()) {
                        val dist = LatLng(it.latitude, it.longitude).distanceTo(
                            LatLng(
                                loc!![0].lat,
                                loc[0].lng
                            )
                        )
                        if (dist < 8) {
                            val feature = featureCollection?.features()
                                ?.find { feature -> feature.getProperty(p.id) != null }
                            features.add(feature!!)
                        }
                    }

                }
                val source: GeoJsonSource? =
                    mapboxMap?.style?.getSourceAs("animate_source")
                source?.let {
                    if (features.isNotEmpty()) {
                        source.setGeoJson(
                            FeatureCollection.fromFeature(
                                features[0]
                            )
                        )
                        homeViewModel.selectMarker(selectedMarkerSymbolLayer)
                    }

                }

            }
        }
    }


    private fun refreshSource() {
        if (homeViewModel.geoJsonFoodSource != null && featureCollection != null) {
            homeViewModel.geoJsonFoodSource?.setGeoJson(featureCollection)
        }
    }


    private fun previewNavigation(newPoiModel: Poi) {
        mapViewModel.invalidate()
        mapViewModel.destinationPoi(newPoiModel)
        binding.layoutNavigation.layoutNavigation.visibility = View.VISIBLE
        val destinationName = when (LocaleManager.getLanguage(requireContext())) {
            LocaleManager.LANGUAGE_ARABIC -> {

                if (newPoiModel.poiMultilingual?.arabic?.name.isNullOrEmpty()) {
                    newPoiModel.poiMultilingual?.en?.name
                } else
                    newPoiModel.poiMultilingual?.arabic?.name
            }

            else -> {
                newPoiModel.poiMultilingual?.en?.name

            }
        }
        binding.layoutNavigation.textDestination.text = destinationName
        mainActivity.setToolBarVisibility(View.GONE)
        mainActivity.setSearchVisibility(View.GONE)
        binding.layoutNavigation.btnArMode.visibility = View.GONE
        binding.layoutNavigation.btnStartNavigation.setOnClickListener(this)
        binding.layoutNavigation.buttonCancel.setOnClickListener(this)
        binding.layoutNavigation.imgBack.setOnClickListener(this)
        binding.layoutNavigation.btnSteps.setOnClickListener(this)
        binding.layoutNavigation.fabRecenter.setOnClickListener(this)
        binding.layoutNavigation.btnArMode.setOnClickListener(this)
        binding.layoutNavigation.btnSpeak.setOnClickListener(this)
        binding.layoutNavigation.btnStopNavigation.setOnClickListener(this)


    }

    @ExperimentalCoroutinesApi
    private suspend fun findNearPoi(query: IALocation) {
        searchChannel.send(query)
    }


    private fun drawRoute(routes: JSONArray?) {
        listOfLatLng.clear()
        val latLngs = ArrayList<Point>()
        var distance = 0.0
        var prevItem: JSONObject? = null
        val features = ArrayList<Feature>()
        for (i in (routes?.length()!! - 1) downTo 0) {

            val item = routes.getJSONObject(i)
            val latLngLeg = LatLng(item.getDouble("lat"), item.getDouble("lng"))
            var nextLatLng: LatLng? = null
            if (i > 0) {
                val nextItem = routes.getJSONObject(i - 1)
                nextLatLng = LatLng(nextItem.getDouble("lat"), nextItem.getDouble("lng"))
            }

            if (prevItem != null) {

                val latLngBeg = LatLng(prevItem.getDouble("lat"), prevItem.getDouble("lng"))
                distance += latLngBeg.distanceTo(latLngLeg)
                latLngs.add(Point.fromLngLat(prevItem.getDouble("lng"), prevItem.getDouble("lat")))
                listOfLatLng.add(LatLng(prevItem.getDouble("lat"), prevItem.getDouble("lng")))
                nextLatLng?.let {

                    val turn = GeoCentricUtil.angleBetweenLines(
                        latLngBeg,
                        LatLng(latLngLeg),
                        LatLng(nextLatLng)
                    )
                    val feature = Feature.fromGeometry(
                        Point.fromLngLat(latLngLeg.longitude, latLngLeg.latitude)
                    )

                    if (0 < turn && turn < 180) {
                        //right
                        if (i == routes.length() - 2) {
//                            text = "Your location is to the right "
                        } else {
                            if (turn <= 120) {
                                feature.addStringProperty(
                                    "image",
                                    R.drawable.poi_route_right.toString()
                                )
                            }
                        }
                    } else {
                        //left
                        if (i == routes.length() - 2) {
//                            text = "Your location is to the left"
                        } else {
                            if (turn >= 240) {
                                feature.addStringProperty(
                                    "image",
                                    R.drawable.poi_route_left.toString()
                                )
                            }
                        }
                    }
                    features.add(feature)

                }

            }
            prevItem = item

        }
        poi?.let {
            val loc = it.locations
            if (loc.isNullOrEmpty().not()) {
                latLngs.add(Point.fromLngLat(loc!![0].lng, loc[0].lat))
                listOfLatLng.add(LatLng(loc[0].lat, loc[0].lng))
            }
        }


        homeViewModel.addLineLayer(
            mapboxMap!!,
            "line_source_route",
            "line_layer_route",
            latLngs,
            requireContext()
        )
        homeViewModel.addLineDirectionSymbolLayer(
            mapboxMap!!,
            "dir_source_route",
            "dir_layer_route",
            features
        )
        updateEta(distance)

    }

    private fun animateCamera(latLng: LatLng, zoom: Double = 17.0, tilt: Double = 60.0) {
        val position = CameraPosition.Builder()
            .target(latLng) // Sets the new camera position
            .zoom(zoom) // Sets the zoom
//            .bearing(tilt) // Rotate the camera
            .tilt(tilt) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder


        mapboxMap!!.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(position)
        )
    }

    private fun updateEta(distanceMeter: Double) {

        val speedIs60MetersPerMinute = 60
        var estimatedDriveTimeInMinutes = distanceMeter / speedIs60MetersPerMinute
        var meter = getString(R.string.meter)
        // for turn by turn change image and all tha
        try {
            if (distanceMeter > 4) {// keeping 2 meter because it in tested on place
                meter = getString(R.string.meters)
            } else {
                val poiName = when (LocaleManager.getLanguage(requireContext())) {
                    LocaleManager.LANGUAGE_ARABIC -> {
                        poi?.poiMultilingual?.arabic?.name

                    }

                    else -> {
                        poi?.poiMultilingual?.en?.name

                    }
                }
                binding.layoutNavigation.etaTextOrigin.text =
                    getString(R.string.arrived_at, poiName)
//                    "You have Arrived at ${poi?.poiMultilingual?.en?.name}"

                Handler(Looper.getMainLooper()).postDelayed({
                    stopNavigation()
                    val directions = HomeFragmentDirections.actionTripSummaryDialogFragment(
                        poi,
                        totalDistance.toFloat()
                    )
                    findNavController().navigate(directions)
                }, 3000)
            }
            var timeUnit = getString(R.string.mins)
            if (estimatedDriveTimeInMinutes < 1) {
                timeUnit = getString(R.string.sec)
                estimatedDriveTimeInMinutes *= 60
            }

            val spanText = SpannableStringBuilder()
                .color(ContextCompat.getColor(requireContext(), R.color.purple_700)) {
                    bold {
                        append(
                            String.format(
                                Locale.US,
                                "%.0f",
                                estimatedDriveTimeInMinutes
                            ) + timeUnit + " | "
                        )
                    }
                }
                .append(String.format(Locale.US, "%.0f", distanceMeter) + meter)
            binding.layoutNavigation.textEta.text = spanText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start_navigation -> {
                binding.layoutNavigation.btnStartNavigation.visibility = View.GONE
                binding.layoutNavigation.btnStopNavigation.visibility = View.VISIBLE
                binding.layoutNavigation.btnSpeak.visibility = View.VISIBLE
                mainActivity.setSearchVisibility(View.GONE)
                if (SessionUtils.checkIsSupportedDeviceOrFinish(requireActivity())) {
                    binding.layoutNavigation.btnArMode.visibility = View.VISIBLE
                }
                mapboxMap?.locationComponent?.cameraMode = CameraMode.TRACKING_COMPASS
                mapboxMap?.locationComponent?.renderMode = RenderMode.COMPASS
                updateTurnByTurn(getString(R.string.straight))
            }
            R.id.btn_stop_navigation -> {

                stopNavigation()

            }
            R.id.button_cancel -> {
                stopNavigation()
            }
            R.id.img_back -> {
                stopNavigation()
            }
            R.id.fab_recenter -> {

                currentLocation?.let {
                    animateCamera(LatLng(it.latitude, it.longitude), 17.5)
                }

            }

            R.id.btn_speak -> {
                val pref = PreferenceHelper.defaultPrefs(requireContext())
                var isSpeaking: Boolean? = pref[AppConstant.IS_SPEAKING]
                pref[AppConstant.IS_SPEAKING] = isSpeaking?.not()
                isSpeaking = pref[AppConstant.IS_SPEAKING]
                mapViewModel.isSpeaking = isSpeaking!!
                if (isSpeaking) {
                    binding.layoutNavigation.btnSpeak.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_volume, null)
                } else
                    binding.layoutNavigation.btnSpeak.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_volume_x, null)
            }
            R.id.btn_steps -> {
                isHideToolbar = true
                val bundle = Bundle()
                bundle.putString(Intent.EXTRA_TEXT, etaTotalTime)
                bundle.putParcelableArrayList("navDetail", mapViewModel.navDetailList)
                findNavController().navigate(R.id.stepsFragment, bundle)

            }

            R.id.btn_ar_mode -> {
                val intent = Intent(context, ArActivity::class.java)
                val wayfindingRequest = IAWayfindingRequest.Builder()
                    .withFloor(0) // destination floor number
                    .withLatitude(poi?.locations?.get(0)?.lat!!) // destination latitude
                    .withLongitude(poi?.locations?.get(0)?.lng!!) // destination longitude
                    .build()
                intent.putExtra("iaArWayfindingTarget", wayfindingRequest)
                intent.putParcelableArrayListExtra("lat_lngs", listOfLatLng)
                startActivity(intent)
//                val direction = HomeFragmentDirections.actionNavigationFragment(poi)
//                findNavController().navigate(direction)
            }
        }
    }


    fun stopNavigation() {

        mapboxMap?.addOnMapClickListener(this)
        removeStartPoi()
        mapboxMap?.locationComponent?.renderMode = RenderMode.NORMAL
        mapboxMap?.locationComponent?.cameraMode = CameraMode.NONE

        if (mapboxMap?.style?.getLayer("line_layer_route") != null) {

            mapboxMap?.style?.removeLayer("line_layer_route")
            mapboxMap?.style?.removeSource("line_source_route")
        }
        if (mapboxMap?.style?.getLayer("dir_layer_route") != null) {
            mapboxMap?.style?.removeLayer("dir_layer_route")
            mapboxMap?.style?.removeSource("dir_source_route")
        }
        mapViewModel.invalidate()
        binding.layoutNavigation.btnArMode.visibility = View.GONE
        binding.layoutNavigation.layoutEta.visibility = View.GONE
        binding.layoutNavigation.layoutStopsBoard.visibility = View.VISIBLE
        binding.layoutNavigation.textDisable.visibility = View.VISIBLE
        binding.layoutNavigation.layoutNavigation.visibility = View.GONE
        binding.layoutNavigation.btnStopNavigation.visibility = View.GONE
        binding.layoutNavigation.btnSpeak.visibility = View.GONE
        binding.layoutNavigation.btnStartNavigation.visibility = View.VISIBLE

        poi = null
        mainActivity.setSearchVisibility(View.VISIBLE)
        mainActivity.setToolBarVisibility(View.VISIBLE)

    }

    private fun calculateEta(poiDetail: Poi) {
        if (currentLocation != null) {
            try {
                totalDistance = CommonUtils.getDistanceFromLatLonInMeters(
                    currentLocation?.latitude!!, currentLocation?.longitude!!,
                    poiDetail.locations?.get(0)?.lat!!, poiDetail.locations?.get(0)?.lng!!
                )
                val speedIs60MetersPerMinute = 60
                var estimatedDriveTimeInMinutes = totalDistance / speedIs60MetersPerMinute
                var meter = getString(R.string.meter)
                if (totalDistance > 1) {
                    meter = getString(R.string.meters)
                }
                var timeUnit = getString(R.string.mins)
                if (estimatedDriveTimeInMinutes < 1) {
                    timeUnit = getString(R.string.sec)
                    estimatedDriveTimeInMinutes *= 60
                }

                val spanText = SpannableStringBuilder()
                    .color(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorServices
                        )
                    ) {
                        bold {
                            append(
                                String.format(
                                    Locale.US,
                                    "%.0f",
                                    estimatedDriveTimeInMinutes
                                ) + timeUnit + " | "
                            )
                        }
                    }
                    .append(String.format(Locale.US, "%.0f", totalDistance) + meter)

                etaTotalTime = spanText.toString()
                binding.layoutNavigation.textEta.text = spanText
            } catch (e: Exception) {// raised when app is in background
                e.printStackTrace()
            }

        }
    }

    private fun updateTurnByTurn(turn: String) {
        try {
            if (mapboxMap?.locationComponent?.renderMode == RenderMode.COMPASS) {
                binding.layoutNavigation.layoutStopsBoard.visibility = View.GONE
                binding.layoutNavigation.textDisable.visibility = View.GONE
                binding.layoutNavigation.layoutEta.visibility = View.VISIBLE
                val directions = if (turn.contains("left")) {
                    if (turn.contains("slight")) {
                        R.drawable.ic_slight_left
                    } else
                        R.drawable.ic_turn_up_left
                } else {
                    if (turn.contains("slight")) {
                        R.drawable.ic_slight_right
                    } else
                        R.drawable.ic_turn_up_right
                }
                binding.layoutNavigation.imageEta.setImageResource(directions)

                binding.layoutNavigation.imageEta.postDelayed({
                    binding.layoutNavigation.imageEta.setImageResource(R.drawable.ic_arrow_up)
                }, 1500)
                val poiName = when (LocaleManager.getLanguage(requireContext())) {
                    LocaleManager.LANGUAGE_ARABIC -> {
                        poi?.poiMultilingual?.arabic?.name

                    }

                    else -> {
                        poi?.poiMultilingual?.en?.name

                    }
                }
                binding.layoutNavigation.etaTextOrigin.text =
                    getString(R.string.walking_to, poiName)
                binding.layoutNavigation.etaTextRouteDetail.text = turn
                if (!turn.contains("location", true)) {
                    binding.layoutNavigation.etaTextRouteDetail.postDelayed({
                        binding.layoutNavigation.etaTextRouteDetail.text =
                            getString(R.string.Walk_Straight)
                    }, 2000)
                }
            }

        } catch (ise: IllegalStateException) {
            ise.printStackTrace()
        }

    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
// Check if permissions are enabled and if not request

        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            showEnableLocationSetting()
            mapViewModel.setupIA(requireContext())
            homeViewModel.enableLocationComponent(mapboxMap!!, requireActivity())
        }
    }


    override fun onMapClick(point: LatLng): Boolean {

        val pointF = mapboxMap?.projection?.toScreenLocation(point)
        val features = mapboxMap?.queryRenderedFeatures(pointF!!, "layer_floor_1")
        val featuresCallOut = mapboxMap?.queryRenderedFeatures(pointF!!, CALLOUT_LAYER_ID)
        if (featuresCallOut.isNullOrEmpty()) {
            if (features?.isNotEmpty()!! && poiList != null) {


                for (feature in features) {
                    for (poi in poiList!!) {
                        if (feature.getProperty(poi.id) != null) {
                            poiFeature = feature

                            this.poi = poi


                            lifecycleScope.launch {
                                updateCallOutFlag(feature)
                                refreshSource()
                            }
                            animateCamera(
                                LatLng(poi.locations!![0].lat, poi.locations!![0].lng),
                                mapboxMap?.cameraPosition?.zoom!!, mapboxMap?.cameraPosition?.tilt!!
                            )

//                            val intent = Intent(context, SymbolLayerMapillaryActivity::class.java)

//                            startActivity(intent)
//                            val direction =
//                                HomeFragmentDirections.actionPoiDetailFragment(poi)
//                            navigateSafe(direction)
//                            findNavController().navigate(direction)
                            break
                        }
                    }

                }
            } else {
                val mapClickFeature = mapboxMap?.queryRenderedFeatures(pointF!!)
                lifecycleScope.launch {
                    mapClickFeature?.let { mapFeature ->
                        if (mapFeature.isNotEmpty()) {
                            updateCallOutFlag(mapClickFeature[0], true)
                            refreshSource()

                        }

                    }

                }

                Log.d("TAG", "onMapClick: empty feature")
            }

        } else {
            val feature = featuresCallOut[0]
            val symbolScreenPoint =
                mapboxMap!!.projection.toScreenLocation(convertToLatLng(feature))
            handleClickCallout(feature, pointF!!, symbolScreenPoint)

        }

        return true
    }

    private suspend fun updateCallOutFlag(feature: Feature, flag: Boolean = false) =
        withContext(Dispatchers.IO) {
            val featureList: List<Feature> = featureCollection?.features()!!
            for (i in featureList.indices) {
                if (featureList[i].getStringProperty("id")
                        .equals(feature.getStringProperty("id")) && !flag
                ) {

                    featureCollection?.features()?.get(i)
                        ?.addBooleanProperty(PROPERTY_SELECTED, true)

                } else {
                    featureCollection?.features()?.get(i)
                        ?.addBooleanProperty(PROPERTY_SELECTED, false)
                }

//            val pro = featureCollection?.features()?.get(i)?.properties()!![PROPERTY_SELECTED]

//            feature.addBooleanProperty(PROPERTY_ICON_OVERLAP, flag)

            }

        }

    private fun convertToLatLng(feature: Feature): LatLng {
        val symbolPoint = feature.geometry() as Point?
        return LatLng(symbolPoint!!.latitude(), symbolPoint.longitude())
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapViewModel.setupIA(requireContext())
            mapboxMap?.getStyle {
                enableLocationComponent()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    //@ExperimentalCoroutinesApi
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if (activity is MainActivity && app.poi == null && !isHideToolbar) {
            mainActivity.setSearchVisibility(View.VISIBLE)
            mainActivity.setToolBarVisibility(View.VISIBLE)
        }

        binding.toggleButtonHome.check(R.id.btn_map_3d_view)
        binding.toggleButtonHome.uncheck(R.id.btn_ar_view)

        app.poi?.let {
            poi = it // if user directly draw route from search
            mapViewModel.destinationPoi(it)
            mapboxMap?.removeOnMapClickListener(this)
            addStartPoi()
            previewNavigation(it)
            calculateEta(it)
            app.poi = null

        }
        mapViewModel.iaLocation.observe(viewLifecycleOwner, {

            currentLocation = it?.toLocation()
            lifecycleScope.launch {
                if (poiList != null) {
                    findNearPoi(it!!)
                }

            }

            mapboxMap?.locationComponent
                ?.forceLocationUpdate(it?.toLocation())
            poi?.locations?.let { poiLoc ->
                if (poiLoc.isNotEmpty()) {
                    val destLatLng = LatLng(poiLoc[0].lat, poiLoc[0].lng)
                    val distanceMeter =
                        LatLng(
                            currentLocation?.latitude!!,
                            currentLocation?.longitude!!
                        ).distanceTo(
                            destLatLng
                        )
                    updateEta(distanceMeter)
                }

            }
        })
        mapViewModel.reRoute.observe(viewLifecycleOwner, {
            poi?.let {
                mapViewModel.destinationPoi(it)
                mapboxMap?.removeOnMapClickListener(this)
                addStartPoi()
                previewNavigation(it)
                calculateEta(it)

            }
        })
        if (isHideToolbar) {
            mainActivity.setToolBarVisibility(View.GONE)
            isHideToolbar = false
        }
    }

    private fun addStartPoi() {
        val name = poiFeature?.let {
            poiFeature?.getStringProperty("image")
        } ?: kotlin.run {
            R.drawable.poi_end.toString()
        }

        currentLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            mapboxMap?.let { mapbox ->
                val poiLatLng =
                    LatLng(poi?.locations?.get(0)?.lat!!, poi?.locations?.get(0)?.lng!!)

                val features = ArrayList<Feature>()
                val feature = Feature.fromGeometry(
                    Point.fromLngLat(latLng.longitude, latLng.latitude)
                )

                val startPoiImg =
                    if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) {
                        feature.addStringProperty("name", getString(R.string.start))
                        R.drawable.poi_start_empty.toString()
                    } else {
                        feature.addStringProperty("name", "")
                        R.drawable.poi_start.toString()
                    }
                feature.addStringProperty("image", startPoiImg)

                val featureEnd = Feature.fromGeometry(
                    Point.fromLngLat(poiLatLng.longitude, poiLatLng.latitude)
                )

                featureEnd.addStringProperty("image", "end_$name")

                featureEnd.addStringProperty("name", "")
                features.add(feature)
                features.add(featureEnd)
                homeViewModel.addStartSymbolLayer(
                    mapbox, "source_start_poi",
                    "layer_start_poi", features, requireContext()
                )
            }
        }

    }

    private fun removeStartPoi() {
        mapboxMap?.let {
            if (it.style?.getLayer("layer_start_poi") != null) {
                it.style?.removeLayer("layer_start_poi")
                it.style?.removeSource("source_start_poi")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isHideToolbar = isNavigationLayoutVisible()

        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onDestroy() {
        super.onDestroy()
        // due to prevent back loading instead on onDestroyView called in onDestroy
        binding.mapView.onDestroy()
        _binding = null
    }

    var imagesMap: HashMap<String, Bitmap> = HashMap()
    private suspend fun generateInfoWindow(featureCollection: FeatureCollection): HashMap<String, Bitmap> =
        withContext(Dispatchers.IO) {
            viewMap.clear()
            imagesMap.clear()
            val nameKey =
                if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) "name_ar" else "name"
            for (feature in featureCollection.features()!!) {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.layout_poi_info_window, null) as BubbleLayout
                val textPoi = view.findViewById<TextView>(R.id.text_poi_name)

                textPoi.text = feature.getStringProperty(nameKey)
                val poiId = feature.getStringProperty("id")

                val bitmap: Bitmap = CommonUtils.generate(view)!!
//            val button = view.findViewById<Button>(R.id.button_navigate)
                viewMap[poiId] = view
                imagesMap[poiId] = bitmap
            }
            imagesMap
        }

    private fun setImageGenResults(imageMap: HashMap<String, Bitmap>) {
        mapboxMap!!.getStyle { style -> // calling addImages is faster as separate addImage calls for each bitmap.
            style.addImages(imageMap)

        }
        // need to store reference to views to be able to use them as hitboxes for click events.

    }

    private fun handleClickCallout(
        feature: Feature,
        screenPoint: PointF,
        symbolScreenPoint: PointF
    ) {
        val view = viewMap[feature.getStringProperty("id")]
        val btnNavigate = view!!.findViewById<ImageButton>(R.id.button_navigate)
        val poiDetailButton = view.findViewById<ImageButton>(R.id.button_poi_detail)
//        val infoText = view.findViewById<TextView>(R.id.text_poi_name)

        // create hitbox for textView
        val hitRectText = Rect()
        btnNavigate.getHitRect(hitRectText)
        val poiDetailRect = Rect()
        poiDetailButton.getHitRect(poiDetailRect)

        // move hitbox to location of symbol
        hitRectText.offset(symbolScreenPoint.x.toInt(), symbolScreenPoint.y.toInt())
        poiDetailRect.offset(symbolScreenPoint.x.toInt(), symbolScreenPoint.y.toInt())


        // offset vertically to match anchor behaviour
        hitRectText.offset(0, -view.measuredHeight)
        poiDetailRect.offset(0, -view.measuredHeight)

        // hit test if clicked point is in textview hitbox
        when {
            hitRectText.contains(screenPoint.x.toInt(), screenPoint.y.toInt()) -> {
                mapboxMap?.removeOnMapClickListener(this)
                mapViewModel.destinationPoi(poi!!)
                addStartPoi()
                previewNavigation(poi!!)
                calculateEta(poi!!)
                lifecycleScope.launch {
                    updateCallOutFlag(feature, true)
                    refreshSource()
                }


            }
            poiDetailRect.contains(screenPoint.x.toInt(), screenPoint.y.toInt()) -> {
                lifecycleScope.launch {
                    updateCallOutFlag(feature, true)
                    refreshSource()
                }
                val direction = HomeFragmentDirections.actionPoiDetailFragment(poi)
                findNavController().navigate(direction)

            }
            else -> {
                // user clicked on icon
//                val featureList: List<Feature> = featureCollection?.features()!!
                /* for (i in featureList.indices) {
                     if (featureList[i].getStringProperty("name")
                             .equals(feature.getStringProperty("name"))
                     ) {
                         featureCollection?.features()?.get(i)
                             ?.addBooleanProperty(PROPERTY_SELECTED, true)


                         refreshSource()
                         Log.d(
                             "TAG",
                             "handleClickCallout: ${feature.getBooleanProperty(PROPERTY_SELECTED)}"
                         )
     //                    toggleFavourite(i)
                     } else {
                         featureCollection?.features()?.get(i)
                             ?.addBooleanProperty(PROPERTY_SELECTED, false)
                     }
                 }*/
            }
        }
    }

    private fun shapeDrawable(@DrawableRes catName: Int): Drawable {
        /* val shapeDrawable = ResourcesCompat.getDrawable(
             resources,
             category,
             null
         )*/
        val bitmapDrawable = ResourcesCompat.getDrawable(resources, catName, null)
        val bitmapDrawablePin =
            ResourcesCompat.getDrawable(resources, R.drawable.poi_end_drawable, null)
        val drawableArr = arrayOf(bitmapDrawablePin, bitmapDrawable)
        return LayerDrawable(drawableArr)
    }

}
