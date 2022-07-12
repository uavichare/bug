package com.example.buglibrary.ui.home.ar

import androidx.fragment.app.Fragment
/*import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory*/
import com.example.buglibrary.di.Injectable
/*
import com.dubaiculture.smartguide.utils.GeoCentricUtil
*/

class NavigationFragment : Fragment(), Injectable {

    /*enum class NodeType {
        POI, ROUTE, GATES
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var homeViewModel: HomeViewModel


    lateinit var mapViewModel: MapViewModel
    private var latLngList: ArrayList<LatLng>? = null
    private var currentLocation: Location? = null

    //    private var iaLocation: IALocation? = null
    private var heading: Double = 0.0
    private var poiDetail: Poi? = null
    private lateinit var mapboxMap: MapboxMap
    private var _binding: NavigationFragmentBinding? = null
    private val binding get() = _binding!!
    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    var delay = 15 * 1000 //Delay for 10 seconds.
    private lateinit var mainActivity: MainActivity
    private lateinit var app: AlFahidiWayFindingApp

    private val anchorList = ArrayList<AnchorNode>()
    private val hashMapDirection = HashMap<LatLng, Int>()
    private var isRouteAdded = false
    private var isPoiAdded = false
    private var totalDistance = 0.0
    private var poiName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NavigationFragmentBinding.inflate(inflater, container, false)
        app = activity?.application as AlFahidiWayFindingApp
        mainActivity = activity as MainActivity
        mapViewModel = app.mapViewModel
        binding.mapView.onCreate(savedInstanceState)
        poiDetail =
            NavigationFragmentArgs.fromBundle(requireArguments()).poi


        if (poiDetail == null) {
            binding.layoutEta.visibility = View.GONE
            mainActivity.setSearchVisibility(View.GONE)
        } else {
            poiName = when (LocaleManager.getLanguage(requireContext())) {
                LocaleManager.LANGUAGE_ARABIC -> {
                    poiDetail?.poiMultilingual?.arabic?.name
                }

                else -> {
                    poiDetail?.poiMultilingual?.en?.name

                }
            }
        }
        binding.mapView.getMapAsync {
            setMapBox(it)
        }
        homeViewModel.heading.observe(viewLifecycleOwner, {
            heading = it.toDouble()
        })

//        binding.textMsg.text = "Detecting Horizontal plane for drawing route"
        askMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            )
        )
        setUpAr()
        binding.btnExitAr.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        *//*  handler.postDelayed(Runnable {
              //do something
              if (isRouteAdded) {
                  *//**//* binding.textMsg.visibility = View.VISIBLE
                 binding.textMsg.text =
                     "Route updated"
                 binding.textMsg.postDelayed(
                     { binding.textMsg.visibility = View.GONE },
                     2000
                 )*//**//*
                redrawAnchor()
            }
            handler.postDelayed(runnable!!, delay.toLong())
        }.also { runnable = it }, delay.toLong())*//*

        return binding.root
    }

*//*
    private fun redrawAnchor() {
        anchorList.forEach {
//            it.worldModelMatrix
            it.anchor?.detach()
        }
        anchorList.clear()
        isRouteAdded = false
        poiDetail?.let { poi ->
            mapViewModel.invalidate()
            mapViewModel.destinationPoi(poi)
        }
    }
*//*

    private fun addGate() {
//        val arrayList = ArrayList<LatLng>()
//        arrayList.add(LatLng(25.263636, 55.300359))// Main entrance
//
//        arrayList.add(LatLng(25.263786, 55.300441))//  Entrance 2
//        arrayList.add(LatLng(25.264327, 55.300808)) // Entrance 3
//        arrayList.add(LatLng(25.264633, 55.299955)) // Entrance 4
//        arrayList.add(LatLng(25.263972, 55.299340))  //Entrance 5
//        arrayList.forEach {
//            addAnchor(it, NodeType.GATES)
//        }

    }

    private fun addPois() {
        homeViewModel.getAllPoi().observe(viewLifecycleOwner, {

            when (it.status) {
                Result.Status.SUCCESS -> {

                    it.data?.let { poiList ->
                        poiList.forEach { poi ->
                            poi.locations?.let { location ->
                                val latLng = LatLng(location[0].lat, location[0].lng)
                                if (currentLocation != null) {
                                    addAnchor(latLng, NodeType.POI, poi)
                                }

                            }
                        }
                    }
                }
                else -> {

                }
            }

        })
    }

    private fun setMapBox(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        val uiSettings: UiSettings = mapboxMap.uiSettings
        uiSettings.isCompassEnabled = false
//            uiSettings.setAttributionTintColor(android.graphics.Color.TRANSPARENT)
        uiSettings.isAttributionEnabled = false
        uiSettings.isLogoEnabled = false
        mapboxMap.setStyle(
            Style.Builder()
                .fromUri(getString(R.string.mapbox_style_url))
        ) {

            enableLocationComponent()
            homeViewModel.mapPoi(getString(R.string.app_token))
                .observe(viewLifecycleOwner, Observer { result ->
                    when (result.status) {
                        Result.Status.SUCCESS -> {
                            if (result.data.isNullOrEmpty().not()) {
                                lifecycleScope.launch {
                                    val features = homeViewModel.poiFeatureList(result.data!!)
                                    homeViewModel.addSymbolLayer(
                                        mapboxMap, "source_floor_1",
                                        "layer_floor_1", features,
                                        requireContext()
                                    )
                                }
                            }

                        }
                        Result.Status.ERROR -> {
                            Log.d("TAG", "setMapBox: error ")
                        }
                        Result.Status.LOADING -> {

                        }

                    }
                })

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModel.routePath.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.length() > 0) {
                    view.postDelayed({ drawRoute(it) }, 300)
                } else {
                    //                Snackbar.make(requireView(), "Sorry No route found", Snackbar.LENGTH_SHORT)
                    //                    .setAnchorView(binding.bottomSheet)
                    //                    .show()
                    //                stopNavigation()

                }
            }
        })
        *//*           binding.mapViewModel.iaLocation.observe(viewLifecycleOwner, Observer {
              *//**//* if (iaLocation == null) {
                 animateCamera(LatLng(it?.latitude!!, it.longitude), 60.0, 17.5)
             }*//**//*
            iaLocation = it
//             updateDot()
            poiDetail?.locations?.let { poiLoc ->
                if (poiLoc.isNotEmpty()) {
                    val destLatLng = LatLng(poiLoc[0].lat, poiLoc[0].lng)
                    val distanceMeter =
                        LatLng(iaLocation?.latitude!!, iaLocation?.longitude!!).distanceTo(
                            destLatLng
                        )
                    updateEta(distanceMeter)
                }

            }
        })

        mapViewModel.heading.observe(viewLifecycleOwner, Observer {
            heading = it
        })
*//*
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
//            mapViewModel.setupIA(requireContext())
            homeViewModel.enableLocationComponent(mapboxMap, requireActivity(), true)
            mapViewModel.iaLocation.observe(viewLifecycleOwner, Observer {

                if (currentLocation == null) {
                    currentLocation = it?.toLocation()
                    poiDetail?.let { poi ->
                        mapViewModel.invalidate()
                        mapViewModel.destinationPoi(poi)
                    }
                }
                currentLocation = it?.toLocation()

                mapboxMap.locationComponent
                    .forceLocationUpdate(it?.toLocation())

                poiDetail?.locations?.let { poiLoc ->
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

        }
    }

    private var isGranted = true
    private val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            for (entry in map.entries) {
                if (entry.value.not()) {
                    isGranted = entry.value
                }
            }
            if (SessionUtils.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with checking "Do not ask again".
                SessionUtils.launchPermissionSettings(requireActivity())
            }

        }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.sceneView.destroy()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
//        handler.removeCallbacks(runnable!!)
        binding.mapView.onPause()
        binding.sceneView.pause()
    }

    *//*   private fun changeFloor(level: Int) {
           if (::mapboxMap.isInitialized.not()) {
               return
           }
           when (level) {
               0 -> {

                   mapboxMap.setStyle(getString(R.string.mapbox_style_url_gf)) {

                   }
               }
               1 -> {

                   mapboxMap.setStyle(getString(R.string.departures_floor)) {
                   }

               }
               2 -> {

                   mapboxMap.setStyle(getString(R.string.mezzanine_floor)) {

                   }

               }
           }

       }
   *//*

    private var session: Session? = null
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if (binding.sceneView.session == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                session = SessionUtils.createArSession(requireActivity(), false)
                if (session == null) {
                    SessionUtils.hasCameraPermission(requireActivity())
                    return
                } else {
                    binding.sceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                SessionUtils.handleSessionException(requireActivity(), e)
            }
        }

        try {
            binding.sceneView.resume()
        } catch (ex: CameraNotAvailableException) {
            SessionUtils.displayError(requireContext(), "Unable to get camera", ex)

            return
        } catch (ex: FatalException) {

        }
    }


    private fun setUpAr() {
        binding.sceneView.scene?.addOnUpdateListener { frameTime ->
            val frame = binding.sceneView.arFrame
//            binding.handMotionContainer.isVisible = !TrackingState.TRACKING
            if (frame?.camera?.trackingState != TrackingState.TRACKING) {
                if (!isRouteAdded)
                    binding.handMotionContainer.isVisible = true
                return@addOnUpdateListener
            }
            if (isRouteAdded) {
                return@addOnUpdateListener
            }

            //get the trackables to ensure planes are detected
            val var3 = frame.getUpdatedTrackables(Plane::class.java).iterator()
            while (var3.hasNext()) {
                val plane = var3.next() as Plane
                //Get all added anchors to the frame

                if (plane.trackingState == TrackingState.TRACKING) {
                    if (anchorList.isNotEmpty()) {
                        anchorList.forEach {
                            it.onUpdate(frameTime)
                        }
                    }
                    binding.handMotionContainer.isVisible = false
                    val iterableAnchor = frame.updatedAnchors.iterator()
//                 hide plane discovery dots
                    //place the first object only if no previous anchors were added
                    if (!iterableAnchor.hasNext() && !isRouteAdded) {
                        if (!isPoiAdded) {
                            addPois()
//                            addGate()
                            isPoiAdded = true
                        }

                        latLngList?.let { list ->
                            if (currentLocation == null) {
                                Toast.makeText(
                                    context,
                                    "Your loc is not found please enable GPS",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@addOnUpdateListener
                            }
                            list.forEach {
                                anchorList.add(addAnchor(it, NodeType.ROUTE))
                            }

                            anchorList.forEachIndexed { index, anchorNode ->
                                if (index < anchorList.size - 1)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        drawLine(anchorNode, anchorList[index + 1])

                                    }
                            }



                            isRouteAdded = true
                        }
                    }
                }
            }

        }

    }


    private fun addAnchor(latLng: LatLng, nodeType: NodeType, poi: Poi? = null): AnchorNode {
        val bearing = LocationUtils.bearing(
            currentLocation?.latitude!!, currentLocation?.longitude!!,
            latLng.latitude, latLng.longitude
        )
        val distance = LocationUtils.getDistanceFromLatLonInMeters(
            currentLocation?.latitude!!, currentLocation?.longitude!!,
            latLng.latitude, latLng.longitude
        )
        var anchorBearing =
            bearing - heading//- Math.toDegrees(orientationAngles[0].toDouble())
        anchorBearing += 360
        anchorBearing %= 360
        val rotation = floor(anchorBearing)
        val rotationRadian = Math.toRadians(rotation)
        val z = -distance
        val zRotated: Float = (z * cos(rotationRadian)).toFloat()

        val xRotated: Float = -(z * sin(rotationRadian)).toFloat()

        val cameraPos = binding.sceneView.scene?.camera?.worldPosition

        val cameraForward = binding.sceneView.scene?.camera?.down
        val position = Vector3.add(cameraPos, cameraForward)
//        val translation = Pose.makeTranslation(xRotated, position.y, zRotated)
        val pos = floatArrayOf(xRotated, position.y, zRotated)
        val poseRotation = floatArrayOf(0f, 0f, 0f, 0f)
        val translation = Pose(pos, poseRotation)
        // Create an ARCore Anchor at the position.

        val pose = Pose.makeTranslation(position.x, position.y, position.z)
        val worldPose = pose?.compose(translation)?.extractTranslation()
        val initAnchor = binding.sceneView.session?.createAnchor(
            worldPose
        )
        val anchorNode = when (nodeType) {
            NodeType.POI -> {
                PoiNode(this@NavigationFragment, poi!!, distance.toFloat(), initAnchor)
            }
            NodeType.GATES -> {
                GateNode(requireContext(), initAnchor)
            }
            else -> {

//             val clamp =    MathHelper.clamp(12.0f, 0.0f, 1.0f)
                val value = hashMapDirection[latLng] ?: 0
//                if (value == 1 || value == 2)
                SignalNode(requireContext(), value, initAnchor)
//                AnchorNode(initAnchor)
            }
        }


//        val anchorNode: AnchorNode = if (isLast) {
//            PoiNode(requireContext(), poiDetail!!, distance.toFloat(), initAnchor)
//        } else {
//            GateNode(requireContext(), initAnchor)
////            SignalNode(requireContext(), null, initAnchor)
//        }
//        val anchorNode = DirectionNode(requireContext(), zRotated, initAnchor)

        anchorNode.setParent(binding.sceneView.scene)
        anchorNode.localPosition = Vector3.zero()
//        Log.d("TAG", "addAnchor: ${anchorNode.worldPosition}")
//        Log.d("TAG", "addAnchor:== ${worldPose!!.tx()} y== ${worldPose.tz()} ")
        return anchorNode
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun drawLine(node1: AnchorNode, node2: AnchorNode) {
        //Draw a line between two AnchorNodes
        val point1 = node1.worldPosition
        val point2 = node2.worldPosition

        //First, find the vector extending between the two points and define a look rotation
        //in terms of this Vector.
        val difference = Vector3.subtract(point1, point2)
        val directionFromTopToBottom = difference.normalized()
        val rotationFromAToB =
            Quaternion.lookRotation(
                directionFromTopToBottom, Vector3.up()
            )
        MaterialFactory.makeOpaqueWithColor(context, Color(0f, 0f, 104f))
            .thenAccept { material: Material? ->
                *//* Then, create a rectangular prism, using ShapeFactory.makeCube() and use the difference vector
                                                       to extend to the necessary length.  *//*
                val model = ShapeFactory.makeCube(
                    Vector3(.25f, .015f, difference.length() + 0.1f),
                    Vector3.zero(), material
                )
                *//* Last, set the world rotation of the node to the rotation calculated earlier and set the world position to
                       the midpoint between the given points . *//*

                val nodeForLine = Node()
                nodeForLine.setParent(node1)
                nodeForLine.renderable = model
//                nodeForLine.localScale = Vector3(1.0f, 1.0f, 0.0f);
                val wVector = Vector3.add(point1, point2)
                (wVector.y - 0.2f).also { wVector.y = it }
                nodeForLine.worldPosition = wVector.scaled(.5f)
                nodeForLine.worldRotation = rotationFromAToB
            }
        MaterialFactory.makeTransparentWithColor(context, Color(255f, 255f, 255f))
            .thenAccept { material: Material? ->
                *//* Then, create a rectangular prism, using ShapeFactory.makeCube() and use the difference vector
                                                       to extend to the necessary length.  *//*

                val model2 = ShapeFactory.makeCube(
                    Vector3(.35f, .01f, difference.length() + 0.1f),
                    Vector3.zero(), material
                )
                *//* Last, set the world rotation of the node to the rotation calculated earlier and set the world position to
                                                       the midpoint between the given points . *//*
                val nodeForLine = Node()
                nodeForLine.setParent(node1)
                nodeForLine.renderable = model2
//                nodeForLine.localPosition = Vector3(0.0f, -1.0f, 0.0f);
                val wVector = Vector3.add(point1, point2)
                (wVector.y - 0.2f).also { wVector.y = it }
                nodeForLine.worldPosition = wVector.scaled(.5f)
                nodeForLine.worldRotation = rotationFromAToB
            }
    }

    private fun drawRoute(routes: JSONArray?) {

        val dotLine = ArrayList<LatLng>()
        var floorlevel = -1
        var distance = 0.0
        var prevItem: JSONObject? = null

        latLngList = ArrayList()
        hashMapDirection.clear()
        for (i in (routes?.length()!! - 1) downTo 0) {

            val item = routes.getJSONObject(i)
            val latLngLeg = LatLng(item.getDouble("lat"), item.getDouble("lng"))

            var nextLatLng: LatLng? = null
            if (i > 0) {
                val nextItem = routes.getJSONObject(i - 1)
                nextLatLng = LatLng(nextItem.getDouble("lat"), nextItem.getDouble("lng"))
            }
            latLngList?.add(latLngLeg)
            val level = item.getString("level").toInt()
            if (prevItem != null) {
                val prevLevel = prevItem.getString("level").toInt()
                val latLngBeg = LatLng(prevItem.getDouble("lat"), prevItem.getDouble("lng"))
                distance += latLngBeg.distanceTo(latLngLeg)
                if (prevLevel != level) {
                    dotLine.add(latLngBeg)
                    dotLine.add(latLngLeg)
                }

                nextLatLng?.let {

                    val turn = GeoCentricUtil.angleBetweenLines(
                        latLngBeg,
                        LatLng(latLngLeg),
                        LatLng(nextLatLng)
                    )


                    if (0 < turn && turn < 180) {
                        //right
                        if (i == routes.length() - 2) {
//                            text = "Your location is to the right "
                        } else {
                            if (turn <= 120) {
                                hashMapDirection[latLngLeg] = 2 // right
                            }
                        }
                    } else {
                        //left
                        if (i == routes.length() - 2) {
//                            text = "Your location is to the left"
                        } else {
                            if (turn >= 240) {
                                hashMapDirection[latLngLeg] = 1 // left

                            }
                        }
                    }


                }
            }

            prevItem = item

        }
        totalDistance = distance
        updateEta(distance)
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
                binding.arrivedText.visibility = View.VISIBLE
                binding.arrivedText.text =
                    getString(R.string.arrived_at, poiName)
                handler.removeCallbacks(runnable!!)
                stopNavigation()
                Handler(Looper.getMainLooper()).postDelayed({
                    val directions = NavigationFragmentDirections.actionTripSummaryDialogFragment(
                        poiDetail,
                        totalDistance.toFloat()
                    )
                    findNavController().navigate(directions)
//                    Navigation.findNavController(requireView()).popBackStack()
                }, 3000)


            }
            var timeUnit = " mins"
            if (estimatedDriveTimeInMinutes < 1) {
                timeUnit = " seconds"
                estimatedDriveTimeInMinutes *= 60
            }

            val spanText = SpannableStringBuilder()
                .color(ContextCompat.getColor(requireContext(), R.color.purple_700)) {
                    bold {
                        append(
                            String.format(
                                java.util.Locale.US,
                                "Going to %s Time %.0f", poiName,
                                estimatedDriveTimeInMinutes
                            ) + timeUnit + " | "
                        )
                    }
                }
                .append(String.format(java.util.Locale.US, "%.0f", distanceMeter) + meter)
            binding.icEstimatedTime.text = spanText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopNavigation() {
        anchorList.forEach {
//            it.worldModelMatrix
            it.anchor?.detach()
        }
        anchorList.clear()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        super.onDestroyView()
        try {
            binding.sceneView.pause()
            binding.sceneView.destroy()
        } catch (e: NullPointerException) {
            Log.e("TAG", "onViewCreated() called ${e.message}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null && ::mapboxMap.isInitialized) {
            binding.mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
*/
}