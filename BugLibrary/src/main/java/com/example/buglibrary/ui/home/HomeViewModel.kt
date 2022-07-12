package com.example.buglibrary.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buglibrary.R
import com.google.gson.Gson
import com.example.buglibrary.data.NavDetails
import com.example.buglibrary.data.Poi
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.AppConstant.DEFAULT_MAX_WAIT_TIME
import com.example.buglibrary.intelligentprovider.Jarvis
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.repo.MapRepository
import com.example.buglibrary.utils.CommonUtils
import com.mapbox.android.core.location.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.CompassListener
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject


const val CALLOUT_LAYER_ID = "mapbox.poi.callout"
const val PROPERTY_SELECTED = "selected"
const val PROPERTY_ICON_OVERLAP = "icon_overlap"

class HomeViewModel @Inject constructor(private val mapRepository: MapRepository) : ViewModel(),
    LocationEngineCallback<LocationEngineResult>, Jarvis.RoutingProvider {

    var i = 0
    var routePath = MutableLiveData<JSONArray>()
    private var previousIndex = -1
    var navDetailList = ArrayList<NavDetails>()
    private lateinit var navDetailJson: JSONObject
    private var locationEngine: LocationEngine? = null
    var locationLiveData = MutableLiveData<Location>()
    private var routeIndex = 0
    private var lineSource: GeoJsonSource? = null
    private lateinit var routeCoordinateList: List<Point>
    private val markerLinePointList = ArrayList<Point>()
    private var currentAnimator: Animator? = null
    private val smartNavigator = Jarvis()
    var heading = MutableLiveData<Float>()
    var geoJsonFoodSource: GeoJsonSource? = null
    private var locationComponent: LocationComponent? = null
    private var customLocationComponentOptions: LocationComponentOptions? = null

    private var lineWidth = 3f

    fun mapPoi(token: String) = mapRepository.pois(token)


    @SuppressLint("MissingPermission")
    fun enableLocationComponent(mapboxMap: MapboxMap, context: Context, isAr: Boolean = false) {
        lineWidth = CommonUtils.pxFromDp(context, 3f)
// Check if permissions are enabled and if not request

        initSmartRoute(this, context)

// Enable the most basic pulsing styling by ONLY using
// the `.pulseEnabled()` method
        val builder = if (isAr) {
            LocationComponentOptions.builder(context)
//            .pulseEnabled(true)
        } else {
            LocationComponentOptions.builder(context)
                .foregroundTintColor(Color.BLACK)
                .foregroundStaleTintColor(Color.BLACK)
        }

        customLocationComponentOptions = builder

            .pulseFadeEnabled(true)
            .build()
        /* if(isAr){
              .foregroundTintColor(Color.BLACK)
                  .bearingTintColor(Color.BLACK)
          }*/


// Get an instance of the component
        locationComponent = mapboxMap.locationComponent


        mapboxMap.getStyle {
            locationComponent?.activateLocationComponent(
                LocationComponentActivationOptions.builder(context, it)
                    .locationEngine(null)
                    .locationEngineRequest(null)
                    .useDefaultLocationEngine(false)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()
            )

            locationComponent?.compassEngine?.addCompassListener(object : CompassListener {
                override fun onCompassChanged(userHeading: Float) {
                    var compassheading = 0f
                    compassheading = if (userHeading < 0.0) {
                        360 + userHeading
                    } else {
                        userHeading
                    }


                    heading.postValue(compassheading)

                }

                override fun onCompassAccuracyChange(compassStatus: Int) {

                }
            })
        }
// Activate with options


// Enable to make component visible
        locationComponent?.isLocationComponentEnabled = true

        if (isAr) {
            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.TRACKING_COMPASS
            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.GPS
        } else {
            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.NONE

            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.NORMAL
        }

//        initLocationEngine(context)

    }

    fun poiImageMap(): IntArray {
        return intArrayOf(
            R.drawable.atm, R.drawable.restaurants_cafe, R.drawable.mosque,
            R.drawable.valet, R.drawable.mosque, R.drawable.parking,
            R.drawable.food_beverages_vendors, R.drawable.tourist_shuttle_bus,
            R.drawable.female_washroom, R.drawable.male_washroom, R.drawable.amenities_toilet,
            R.drawable.artistic_houses, R.drawable.frame,
            R.drawable.poi_start, R.drawable.poi_start_empty, R.drawable.poi_end,
            R.drawable.institution, R.drawable.poi_route_left,
            R.drawable.poi_route_right,
            R.drawable.hotel, R.drawable.retail,

            )
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private fun initLocationEngine(context: Context) {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        val request = LocationEngineRequest.Builder(AppConstant.DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()


//        locationEngine?.requestLocationUpdates(request, this, Looper.getMainLooper())
//        locationEngine?.getLastLocation(this)
    }

    override fun onSuccess(result: LocationEngineResult?) {
        val location = result?.lastLocation ?: return

//        val test = Location("test")
//        test.latitude = 25.264181
//        test.longitude = 55.300155
//        test.bearingAccuracyDegrees = location.bearingAccuracyDegrees
        locationLiveData.postValue(location)
        smartNavigator.onLocationUpdate(LatLng(location.latitude, location.longitude), 0)
    }

    override fun onFailure(exception: Exception) {

    }

    private fun initSmartRoute(provider: Jarvis.RoutingProvider, context: Context) {
        smartNavigator.initSmartRouter(context, provider)
    }

    override fun onReRoute() {


    }

    override fun currIndex(index: Int) {
        if (previousIndex == index) {
            return
        }
        /*   var turn = ""
           if (::navDetailJson.isInitialized) {
               try {
                   val jsonArray = navDetailJson.getJSONArray(index.toString())
                   if (jsonArray.length() >= 1) {
                       turn = jsonArray[0].toString()
                   }
               } catch (e: JSONException) {
                   e.printStackTrace()
               }
           }*/
        previousIndex = index


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


/*    fun destinationPoi(poi: Poi, context: Context) {

        val poiLatLng =
            LatLng(poi.locations?.get(0)?.lat!!, poi.locations?.get(0)?.lng!!)
        smartNavigator.setRouteDestination(
            poiLatLng,
            poi.locations?.get(0)?.levelid!!.toInt()
        )

//        isReroute = true

    }*/


    private class PointEvaluator : TypeEvaluator<Point?> {

        override fun evaluate(
            fraction: Float,
            startValue: Point?,
            endValue: Point?
        ): Point? {
            return Point.fromLngLat(
                startValue?.longitude()!! + (endValue?.longitude()!! - startValue.longitude()) * fraction,
                startValue.latitude() + (endValue.latitude() - startValue.latitude()) * fraction
            )
        }

    }

    private fun createLatLngAnimator(
        currentPosition: Point,
        targetPosition: Point
    ): Animator {
        val latLngAnimator: ValueAnimator =
            ValueAnimator.ofObject(PointEvaluator(), currentPosition, targetPosition)
        val distance = TurfMeasurement.distance(
            currentPosition,
            targetPosition,
            "meters"
        )
        latLngAnimator.duration = distance.toLong()
        latLngAnimator.interpolator = LinearInterpolator()
        latLngAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animate()
            }
        })
        latLngAnimator.addUpdateListener { animation ->
            val point =
                animation.animatedValue as Point

            markerLinePointList.add(point)
            lineSource?.setGeoJson(
                Feature.fromGeometry(
                    LineString.fromLngLats(
                        markerLinePointList
                    )
                )
            )
        }
        return latLngAnimator
    }

    fun addLineLayer(
        mapboxMap: MapboxMap,
        sourceId: String,
        layerId: String,
        latLngs: ArrayList<Point>,
        context: Context
    ) {
        routeCoordinateList = latLngs
        if (mapboxMap.style?.getLayer(layerId) != null) {
            mapboxMap.style?.removeLayer(layerId)
            mapboxMap.style?.removeSource(sourceId)
        }
        lineSource = GeoJsonSource(
            sourceId,
            FeatureCollection.fromFeatures(
                arrayOf(
                    Feature.fromGeometry(
                        LineString.fromLngLats(latLngs)
                    )
                )
            )
        )
        mapboxMap.style?.addSource(lineSource!!)
        mapboxMap.style?.addLayer(
            LineLayer(layerId, sourceId).withProperties(
                /*  PropertyFactory.lineDasharray(
                      arrayOf(
                          0.01f,
                          2f
                      )
                  ),*/
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(lineWidth),
                PropertyFactory.lineColor(
                    Color.parseColor(
                        "#BC56FF"
                    )
                )
            )
        )
        if (currentAnimator != null) {
            currentAnimator?.cancel()
        }
        animate()

        mapboxMap.getStyle {
            customLocationComponentOptions = LocationComponentOptions.builder(context)
//            .pulseEnabled(true)
                .foregroundTintColor(Color.BLACK)
                .bearingTintColor(Color.BLACK)
                .layerAbove(layerId)
                .pulseFadeEnabled(true)
                .build()
            locationComponent?.applyStyle(customLocationComponentOptions!!)
        }
    }

    private fun animate() {
        // Check if we are at the end of the points list
        if (routeCoordinateList.size - 1 > routeIndex) {
            val indexPoint = routeCoordinateList[routeIndex]
            val newPoint =
                Point.fromLngLat(indexPoint.longitude(), indexPoint.latitude())
            currentAnimator = createLatLngAnimator(indexPoint, newPoint)
            currentAnimator?.start()
            routeIndex++
        }
    }

    private lateinit var markerAnimator: ValueAnimator
    fun selectMarker(iconLayer: SymbolLayer) {
        if (::markerAnimator.isInitialized) {
            markerAnimator.cancel()
        }
        markerAnimator = ValueAnimator()
        markerAnimator.setObjectValues(1f, 0.7f)
        markerAnimator.duration = 700
        markerAnimator.addUpdateListener { animator ->
            iconLayer.setProperties(
                PropertyFactory.iconSize(animator.animatedValue as Float)
            )
        }
        markerAnimator.repeatMode = ValueAnimator.REVERSE
        markerAnimator.repeatCount = 4
        markerAnimator.start()
//        markerSelected = true
    }


    fun addSymbolLayer(
        mapboxMap: MapboxMap, sourceId: String, layerId: String, features: ArrayList<Feature>,
        context: Context
    ) {
        if (mapboxMap.style?.getLayer(layerId) != null) {
            mapboxMap.style?.removeLayer(layerId)
            mapboxMap.style?.removeSource(sourceId)
            geoJsonFoodSource = null
        }
        val options = GeoJsonOptions()
            .withCluster(true)
//            .withClusterRadius(30)
        val foodFeatureCollection = FeatureCollection.fromFeatures(features)
        geoJsonFoodSource = GeoJsonSource(sourceId, foodFeatureCollection, options)


        if (mapboxMap.style?.getSource(sourceId) == null) {
            mapboxMap.style?.addSource(geoJsonFoodSource!!)
        }


        val symbolLayer = SymbolLayer(layerId, sourceId)
        symbolLayer.withProperties(
            PropertyFactory.textField(
                when {
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_ARABIC -> {
                        "{name_ar}"
                    }
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_CHINESE -> {
                        "{name_zh}"
                    }
                    else -> {
                        "{name}"
                    }
                }
            ),

            PropertyFactory.textOffset(arrayOf(0f, -1.4f)),
            PropertyFactory.textLineHeight(0.9f),
            PropertyFactory.textHaloColor(ContextCompat.getColor(context, R.color.symbolHaloColor)),
            PropertyFactory.textHaloWidth(1f),
            PropertyFactory.textJustify(Property.TEXT_JUSTIFY_CENTER),
            PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
            PropertyFactory.textIgnorePlacement(false),
            PropertyFactory.iconImage("{image}"),
            /* PropertyFactory.iconSize(
                 Expression.match(
                     Expression.toString(Expression.get(PROPERTY_SELECTED)),
                     Expression.literal(1.0f),
                     Expression.stop("true", 1.5f)
                 )
             )*/
            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 0.7f)
                )
            ),
            PropertyFactory.iconOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 1f)
                )
            ),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.textAllowOverlap(false),
            PropertyFactory.textSize(
                Expression.interpolate(
                    Expression.linear(),
                    Expression.zoom(), Expression.stop(17.5f, 4f),
                    Expression.stop(18f, 12f)
                )
            ),
            PropertyFactory.textOptional(true),
            PropertyFactory.textColor(
                Expression.match(
                    Expression.get("color"), Expression.color(Color.BLACK),
                    Expression.stop(
                        "service",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.colorServices)
                        )
                    ),
                    Expression.stop(
                        "hot_pink",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.hot_pink)
                        )
                    ),
                    Expression.stop(
                        "green",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.green)
                        )
                    ),
                    Expression.stop(
                        "tangerine",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.tangerine)
                        )
                    ),
                    Expression.stop(
                        "dark pink",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.dark_pink)
                        )
                    ),
                    Expression.stop(
                        "brown",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.brown)
                        )
                    ),
                    Expression.stop(
                        "persian blue", Expression.color(
                            ContextCompat.getColor(context, R.color.persian_blue)
                        )
                    )
                )
            ),
            PropertyFactory.textFont(arrayOf("Dubai Regular"/*, "Baloo Chettan Regular"*/)),
            PropertyFactory.textOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(17.2f, 0f),
                    Expression.stop(18f, 1f)
                )
            )
        )
        /* symbolLayer.setFilter(
             Expression.eq(
                 Expression.get("destination"),
                 Expression.literal(true)
             )
         )*/
        mapboxMap.style?.addLayer(symbolLayer)
    }

    fun addSymbolSelectedLayer(
        mapboxMap: MapboxMap, sourceId: String, layerId: String,
        context: Context
    ) {
        if (mapboxMap.style?.getLayer(layerId) != null) {
            mapboxMap.style?.removeLayer(layerId)
            mapboxMap.style?.removeSource(sourceId)
        }
        if (mapboxMap.style?.getSource(sourceId) == null) {
            mapboxMap.style?.addSource(GeoJsonSource(sourceId))
        }


        val symbolLayer = SymbolLayer(layerId, sourceId)
        symbolLayer.withProperties(
            PropertyFactory.textField(
                when {
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_ARABIC -> {
                        "{name_ar}"
                    }
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_CHINESE -> {
                        "{name_zh}"
                    }
                    else -> {
                        "{name}"
                    }
                }
            ),

            PropertyFactory.textOffset(arrayOf(0f, -1.4f)),
            PropertyFactory.textLineHeight(0.9f),
            PropertyFactory.textHaloColor(ContextCompat.getColor(context, R.color.symbolHaloColor)),
            PropertyFactory.textHaloWidth(1f),
            PropertyFactory.textJustify(Property.TEXT_JUSTIFY_CENTER),
            PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
            PropertyFactory.textIgnorePlacement(false),
            PropertyFactory.iconImage("{image}"),
            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 0.7f)
                )
            ),
            PropertyFactory.iconOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 1f)
                )
            ),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.textAllowOverlap(false),
            PropertyFactory.textSize(
                Expression.interpolate(
                    Expression.linear(),
                    Expression.zoom(), Expression.stop(17.5f, 4f),
                    Expression.stop(18f, 12f)
                )
            ),
            PropertyFactory.textOptional(true),
            PropertyFactory.textColor(
                Expression.match(
                    Expression.get("color"), Expression.color(Color.BLACK),
                    Expression.stop(
                        "service",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.colorServices)
                        )
                    ),
                    Expression.stop(
                        "hot_pink",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.hot_pink)
                        )
                    ),
                    Expression.stop(
                        "green",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.green)
                        )
                    ),
                    Expression.stop(
                        "tangerine",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.tangerine)
                        )
                    ),
                    Expression.stop(
                        "dark pink",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.dark_pink)
                        )
                    ),
                    Expression.stop(
                        "brown",
                        Expression.color(
                            ContextCompat.getColor(context, R.color.brown)
                        )
                    ),
                    Expression.stop(
                        "persian blue", Expression.color(
                            ContextCompat.getColor(context, R.color.persian_blue)
                        )
                    )
                )
            ),
            PropertyFactory.textFont(arrayOf("Dubai Regular"/*, "Baloo Chettan Regular"*/)),
            PropertyFactory.textOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(17.2f, 0f),
                    Expression.stop(18f, 1f)
                )
            )
        )
        mapboxMap.style?.addLayer(symbolLayer)

    }

    fun addStartSymbolLayer(
        mapboxMap: MapboxMap,
        sourceId: String,
        layerId: String,
        features: ArrayList<Feature>, context: Context
    ) {

        if (mapboxMap.style?.getLayer(layerId) != null) {
            mapboxMap.style?.removeLayer(layerId)
            mapboxMap.style?.removeSource(sourceId)
        }


        val startFeatureCollection = FeatureCollection.fromFeatures(features)
        val geoJsonFoodSource = GeoJsonSource(sourceId, startFeatureCollection)
        mapboxMap.style?.addSource(geoJsonFoodSource)
        val symbolLayer = SymbolLayer(layerId, sourceId)
        symbolLayer.withProperties(
            PropertyFactory.iconImage("{image}"),
            PropertyFactory.textField("{name}"),

            PropertyFactory.textOffset(arrayOf(0f, 1f)),
            PropertyFactory.textLineHeight(0.9f),
            PropertyFactory.textColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            ),
            PropertyFactory.textJustify(Property.TEXT_JUSTIFY_CENTER),
            PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
            PropertyFactory.textIgnorePlacement(false),
            PropertyFactory.textAllowOverlap(false),
            PropertyFactory.textSize(
                Expression.interpolate(
                    Expression.linear(),
                    Expression.zoom(), Expression.stop(17.5f, 4f),
                    Expression.stop(17f, 10f)
                )
            ),
            PropertyFactory.textFont(arrayOf("Dubai Regular"/*, "Baloo Chettan Regular"*/)),

            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 0.7f)
                )
            ),
            PropertyFactory.iconOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 1f)
                )
            ),
            PropertyFactory.iconAllowOverlap(true)

        )
        mapboxMap.style?.addLayer(symbolLayer)
    }

    fun addLineDirectionSymbolLayer(
        mapboxMap: MapboxMap, sourceId: String, layerId: String, features: ArrayList<Feature>
    ) {
        if (mapboxMap.style?.getLayer(layerId) != null) {
            mapboxMap.style?.removeLayer(layerId)
            mapboxMap.style?.removeSource(sourceId)
        }
        val options = GeoJsonOptions()
            .withCluster(true)

        val foodFeatureCollection = FeatureCollection.fromFeatures(features)
        val sourceGeo = GeoJsonSource(sourceId, foodFeatureCollection, options)

        if (mapboxMap.style?.getSource(sourceId) == null)
            mapboxMap.style?.addSource(sourceGeo)


        val symbolLayer = SymbolLayer(layerId, sourceId)
        symbolLayer.withProperties(

            PropertyFactory.iconImage("{image}"),
//            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_TOP),

            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 0.7f)
                )
            ),
            PropertyFactory.iconOpacity(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(15f, 0f),
                    Expression.stop(18f, 1f)
                )
            ),
            PropertyFactory.iconAllowOverlap(true)
        )

        mapboxMap.style?.addLayer(symbolLayer)
    }

    fun setupCalloutLayer(mapboxMap: MapboxMap, loadedMapStyle: Style) {
        if (mapboxMap.style?.getLayer(CALLOUT_LAYER_ID) != null) {
            mapboxMap.style?.removeLayer(CALLOUT_LAYER_ID)
        }

        loadedMapStyle.addLayer(
            SymbolLayer(CALLOUT_LAYER_ID, "source_floor_1")
                .withProperties( /* show image with id title based on the value of the title feature property */
                    PropertyFactory.iconImage("{id}"),  /* set anchor of icon to bottom-left */
                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT),  /* offset icon slightly to match bubble layout */
                    PropertyFactory.iconOffset(arrayOf(-20.0f, -10.0f))
                ) /* add a filter to show only when selected feature property is true */
                .withFilter(
                    Expression.eq(
                        Expression.get(PROPERTY_SELECTED),
                        Expression.literal(true)
                    )
                )
        )
    }


    suspend fun poiFeatureList(poiList: List<Poi>): ArrayList<Feature> =
        withContext(Dispatchers.IO) {
            val gson = Gson()
            val features = ArrayList<Feature>()
            poiList.forEach { poi ->

                poi.locations?.forEach {
                    val feature = Feature.fromGeometry(
                        Point.fromLngLat(it.lng, it.lat)
                    )

                    feature.addStringProperty("id", poi.id)
                    feature.addProperty(poi.id, gson.toJsonTree(poi))
                    feature.addStringProperty("level", it.levelid)

                    when (poi.subcategory.toLowerCase(java.util.Locale.ROOT)) {
                        "restaurants" -> {

                            features.add(
                                addFeature(
                                    feature,
                                    poi,
                                    R.drawable.food_beverages_vendors,
                                    "persian blue"
                                )
                            )
                        }
                        "hotels" -> {
                            features.add(addFeature(feature, poi, R.drawable.hotel, "hot_pink"))
                        }
                        "houses of safeguarding the emirati heritage ( museum)" -> {
                            features.add(addFeature(feature, poi, R.drawable.frame, "green"))
                            features.add(feature)
                        }
                        "institutions(office)" -> {
                            features.add(
                                addFeature(
                                    feature,
                                    poi,
                                    R.drawable.institution,
                                    "tangerine"
                                )
                            )
                            features.add(feature)

                        }
                        "artistic houses for professional talents" -> {
                            features.add(
                                addFeature(
                                    feature,
                                    poi,
                                    R.drawable.artistic_houses,
                                    "brown"
                                )
                            )
                            features.add(feature)
                        }
                        "retail" -> {
                            features.add(
                                addFeature(
                                    feature,
                                    poi,
                                    R.drawable.retail,
                                    "dark pink"
                                )
                            )
                            features.add(feature)
                        }
                        "parking" -> {
                            features.add(
                                addFeature(feature, poi, R.drawable.parking, "persian blue")
                            )
                            features.add(feature)
                        }
                        "mosque" -> {
                            features.add(
                                addFeature(feature, poi, R.drawable.mosque, "persian blue")
                            )
                            features.add(feature)
                        }
                        "toilet" -> {
                            features.add(
                                addFeature(feature, poi, R.drawable.toilet, "persian blue")
                            )
                            features.add(feature)
                        }

                    }
                }

            }

            features
        }

    private fun addFeature(
        feature: Feature,
        poi: Poi, imageId: Int, colorName: String
    ): Feature {
        feature.addStringProperty("name", poi.poiMultilingual!!.en.name)
        feature.addStringProperty("name_ar", poi.poiMultilingual!!.arabic.name)
        feature.addStringProperty("name_zh", poi.poiMultilingual!!.chinese.name)
        feature.addStringProperty("color", colorName)
        feature.addBooleanProperty(PROPERTY_SELECTED, false)
        feature.addBooleanProperty(PROPERTY_ICON_OVERLAP, false)
        feature.addStringProperty("image", imageId.toString())
        return feature
    }

    fun getAllPoi() =
        mapRepository.getAllPoi()

    fun invalidate() {
        smartNavigator.invalidateSmartRouter()
    }


    override fun onCleared() {
        super.onCleared()
        if (::markerAnimator.isInitialized) {
            markerAnimator.cancel()
        }
        if (currentAnimator != null) {
            currentAnimator?.cancel()
        }
    }
}