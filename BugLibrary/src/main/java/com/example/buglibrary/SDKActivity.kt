package com.example.buglibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.DubaiActivityMainBinding
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.set
import com.example.buglibrary.helper.PreferenceHelper.get

import com.example.buglibrary.manager.LocaleManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.example.buglibrary.services.GeofenceBroadcastReceiver
import com.example.buglibrary.ui.home.HomeFragment
import com.example.buglibrary.ui.home.SearchAdapter
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject


class SDKActivity : AppCompatActivity(), HasAndroidInjector, PermissionsListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    //  private lateinit var appBarConfiguration: AppBarConfiguration
    private var homeFragment: HomeFragment? = null

    @ExperimentalCoroutinesApi
    private var searchChannel = BroadcastChannel<String>(1)
    private lateinit var binding: DubaiActivityMainBinding

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private var permissionsManager: PermissionsManager? = null
    lateinit var navHostFragment: FragmentContainerView

    // geofence
    lateinit var geofencingClient: GeofencingClient
    private var mGeofenceList: ArrayList<Geofence>? = null
    lateinit var navController: NavController

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */

    private var INSTANCE: SDKActivity? = null

    var BAY_AREA_LANDMARKS: HashMap<String, LatLng> = HashMap()


    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    fun getInstance(): SDKActivity? {
        if (INSTANCE == null) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = SDKActivity()
                }
            }
        }
        return INSTANCE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DubaiActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  viewModel = injectViewModel(viewModelProvider)
       // callMapbox(this,getString(R.string.mapbox_access_token))
       // setIAKEYS(this,"675933d8-45d2-4397-aef6-80bcf5861fed","rxNJoW/xt1iVy3BA5c0r69tjf6097VxsW9dz56JzOnQsRbcD3qGDyKT0e3iA1XGEpn2N5JHL7FpgZSyuF5BKSXXWqJ+Y2nWqr8lXa5lmECBYOxiZzXnCih5Ozljgag==")

       Mapbox.getInstance(this,sharePreferenceObject(this)[AppConstant.MAPBOX_TOKEN])
        BAY_AREA_LANDMARKS["DCAA"] = LatLng(25.264106314899042, 55.30021935514805)
        BAY_AREA_LANDMARKS["home"] = LatLng(20.5448449, 74.5416257)
        mGeofenceList = ArrayList()
        populateGeofenceList()
        geofencingClient = LocationServices.getGeofencingClient(this)

        val notification = intent.getStringExtra(AppConstant.NOTIFICATION_TYPE)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)

        //setIAKEYS()
        navHostFragment = findViewById(R.id.nav_host_fragment)

        // val navController by lazy { findNavController(R.id.nav_host_fragment) }

        navController.setGraph(R.navigation.mobile_navigation)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener {
            navController.popBackStack()

        }

        setPermission()

/*
        val fragmet= navController.currentDestination as? HomeFragment
        // fragmet?.enableLocationComponent(this)
*/

/*
        val pref = PreferenceHelper.defaultPrefs(this)
        val isFirstTime: Boolean? = pref[AppConstant.ON_BOARDING]
        if (!isFirstTime!!) {
//            pref[AppConstant.ON_BOARDING] = true
            val graphInflater = navController.navInflater
            val navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
            navGraph.startDestination = R.id.onBoardingFragment
            navController.graph = navGraph
            setToolBarVisibility(View.GONE)
            setSearchVisibility(View.GONE)
        }
*/

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
/*
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_favourite, R.id.nav_attraction,
                R.id.nav_notification, R.id.nav_help, R.id.nav_happiness_meter,
                R.id.nav_contact_us, R.id.nav_about_us, R.id.nav_share_app,
                R.id.nav_privacy, R.id.nav_language, R.id.nav_settings
            ), binding.drawerLayout
        )
*/
/*
        val navHostFragment1 =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val fragment = navHostFragment1.childFragmentManager.fragments[0]

        if (fragment is HomeFragment) {
            homeFragment = fragment
        }
*/

        binding.appBar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                lifecycleScope.launch {
                    searchText(query.trim())
                }
                return true
            }
        })

        /*val view = binding.navView.getHeaderView(0)
        val navBinding = NavHeaderMainBinding.bind(view)
        navBinding.btnSignOut.setOnClickListener {
            toggleDrawer()
            val direction = HomeFragmentDirections.actionSignUpFragment()
            Navigation.findNavController(navHostFragment)
                .navigate(direction)
            setSearchVisibility(View.GONE)

        }
*/
        /*  navController.addOnDestinationChangedListener { _, destination, _ ->
              if (destination.id != R.id.guideFragment) {
                  setToolBarVisibility(View.VISIBLE)
              }
          }

          if (isFirstTime) {
              setPermission()
              //askSinglePermissions.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
          }
  */
        //setupActionBarWithNavController(navController, appBarConfiguration)
        // binding.navView.setupWithNavController(navController)
        //navItem(navHostFragment)
/*
        notification?.let {
            if (it == "notification") {
                Navigation.findNavController(navHostFragment)
                    .navigate(R.id.nav_notification)
            }
        }
*/
        /*  binding.navView.setNavigationItemSelectedListener {
              Log.d("TAG", "onCreate: clicked")
              when(it.itemId){

              }

              true
          }*/
        configureSearch()

       // setIAKEYS(this,"675933d8-45d2-4397-aef6-80bcf5861fed","rxNJoW/xt1iVy3BA5c0r69tjf6097VxsW9dz56JzOnQsRbcD3qGDyKT0e3iA1XGEpn2N5JHL7FpgZSyuF5BKSXXWqJ+Y2nWqr8lXa5lmECBYOxiZzXnCih5Ozljgag==")


        /*  colourTextHeader(binding.navView.menu.findItem(R.id.account))
          colourTextHeader(binding.navView.menu.findItem(R.id.smart_guide))
          colourTextHeader(binding.navView.menu.findItem(R.id.your_voice))
          colourTextHeader(binding.navView.menu.findItem(R.id.information))
          colourTextHeader(binding.navView.menu.findItem(R.id.settings))
          fcmToken()

  */
    }


    private val askSinglePermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("TAG", "granted : ")
                geofencingClient.removeGeofences(geofencePendingIntent).run {
                    addOnSuccessListener {
                        // Geofences removed
                        // ...
                    }
                    addOnFailureListener {
                        // Failed to remove geofences
                        // ...
                    }
                }

                addGeofence()
            }
        }

/*
    private fun ipDetail(fcmToken: String) {
        viewModel.fetchIpDetail().observe(this, {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    val language = LocaleManager.getLanguage(this)
                    val deviceId = CommonUtils.getDeviceId(this)
                    viewModel.sendUserDetail(it.data!!, fcmToken, language, deviceId)
                        .observe(this, {

                        })
                }
                else -> {
                    //do nothing
                }
            }
        })
    }
*/

    fun setPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }


/*
    private fun fcmToken() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val fcmRegToken = task.result?.toString()

                Log.d("TAG", "fcmToken: $fcmRegToken")
              //  ipDetail(fcmRegToken!!)


            })
    }
*/

    private fun populateGeofenceList() {
        for ((key, value) in BAY_AREA_LANDMARKS.entries) {
            mGeofenceList!!.add(
                Geofence.Builder() // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(key) // Set the circular region of this geofence.
                    .setCircularRegion(
                        value.latitude,
                        value.longitude,
                        AppConstant.GEOFENCE_RADIUS_IN_METERS
                    ) // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(AppConstant.GEOFENCE_EXPIRATION_IN_MILLISECONDS) // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                    ) // Create the geofence.
                    .build()
            )
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(mGeofenceList!!)
        }.build()
    }

    private fun colourTextHeader(smartGuide: MenuItem) {
        val s = SpannableString(smartGuide.title)
        s.setSpan(TextAppearanceSpan(this, R.style.HeaderTextAppearance), 0, s.length, 0)
        smartGuide.title = s
    }


    @ExperimentalCoroutinesApi
    private suspend fun searchText(query: String) {
        searchChannel.send(query)
    }

    @ExperimentalCoroutinesApi
    private fun configureSearch() {
        val content = binding.appBar.layoutMain
        content.recyclerViewSearch.layoutManager = LinearLayoutManager(this)
        val searchAdapter = SearchAdapter(this)
        content.recyclerViewSearch.adapter = searchAdapter
        content.recyclerViewSearch.addItemDecoration(
            DividerItemDecoration(
                this,
                RecyclerView.VERTICAL
            )
        )

        lifecycleScope.launch {
            searchChannel.consumeEach {
                delay(300)
                val list = search(it)
                if (list?.isNotEmpty() == true) {
                    content.recyclerViewSearch.visibility = View.VISIBLE
                    content.placesAroundYou.visibility=View.VISIBLE
                    searchAdapter.submitList(list)

                } else {
                    content.recyclerViewSearch.visibility = View.GONE
                    content.placesAroundYou.visibility=View.GONE

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        val navHostFragment1 =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val fragment = navHostFragment1.childFragmentManager.fragments[0]
        if (fragment is HomeFragment) {
            homeFragment = fragment
        }
        Log.d("TAG", "onCreate: homefragment ${fragment is HomeFragment}")
        homeFragment?.onPermissionResult(granted)
        if (granted) {
/*
            askSinglePermissions.launch(

                Manifest.permission.ACCESS_BACKGROUND_LOCATION

            )
*/
        } else {
            Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show()
        }

    }

    private fun search(query: String): List<Poi>? {
        if (query.isEmpty()) {
            return emptyList()
        }
        val regex = ".*" + query.trim().toLowerCase(Locale.ROOT) + ".*"
        val pattern = Pattern.compile(regex)

        val filterList = navHostFragment.getFragment<HomeFragment>().poiList?.filter {
            pattern.matcher(it.poiMultilingual?.en?.name?.toLowerCase(Locale.ROOT)!!).matches() ||
                    pattern.matcher(it.poiMultilingual?.arabic?.name?.toLowerCase(Locale.ROOT)!!)
                        .matches()
        }
        filterList ?: ArrayList()

        return filterList
    }

/*
    private fun navItem(navHostFragment: FragmentContainerView) {
        val versionMenu = binding.navView.menu.findItem(R.id.nav_log_version)
        val version = versionMenu.actionView.findViewById<TextView>(R.id.version)
        val date = versionMenu.actionView.findViewById<TextView>(R.id.date)
        version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        date.text = dateFormat.format(Date(BuildConfig.BUILD_TIME.toLong()))
*/
/*
        binding.navView.setNavigationItemSelectedListener {
            toggleDrawer()
            if (it.itemId == R.id.nav_attraction) {
                setSearchVisibility(View.VISIBLE)
            } else {
                setSearchVisibility(View.GONE)
            }


            when (it.itemId) {
                R.id.nav_home -> {
                    Navigation.findNavController(navHostFragment).navigate(R.id.nav_home)
                }
                R.id.nav_attraction -> {
                    Navigation.findNavController(navHostFragment).navigate(R.id.nav_attraction)
                }
                R.id.nav_favourite -> {
                    Navigation.findNavController(navHostFragment).navigate(R.id.nav_favourite)
                }
                R.id.nav_notification -> {
                    Navigation.findNavController(navHostFragment)
                        .navigate(R.id.nav_notification)
                }
                R.id.nav_help -> {
                    Navigation.findNavController(navHostFragment)
                        .navigate(R.id.guideFragment)
                }
                R.id.nav_rate_us -> {

*//*

*/
/*
                    IntentUtils.openWebPage(
                        this,
                        "https://play.google.com/store/apps/details?id=com.dubaiculture"
                    )
*//*
*/
/*

                    Snackbar.make(
                        navHostFragment,
                        "This feature will be available once the application goes to production.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                     val manager = ReviewManagerFactory.create(this)
                   // val manager = FakeReviewManager(this)
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { request ->
                        if (request.isSuccessful) {
                            // We got the ReviewInfo object
                            val reviewInfo = request.result
                            val flow = manager.launchReviewFlow(this, reviewInfo)
                            flow.addOnCompleteListener {
                                Toast.makeText(this, "request successfull", Toast.LENGTH_LONG)
                                    .show()

                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                            }
                        }
                    }


                }
                R.id.nav_share_app -> {


*//*

*/
/*
                    IntentUtils.shareText(
                        this,
                        getString(R.string.share_app_text),
                        getString(R.string.app_name)
                    )
*//*
*/
/*



                    createLink(this)
                }
                R.id.nav_related_apps -> {
                    val url =
                        "https://play.google.com/store/apps/collection/cluster?clp=igM1ChkKEzg4MzA2MTUyOTkxMjIxNzYzMzkQCBgDEhYKEGNvbS5kdWJhaWN1bHR1cmUQARgDGAE%3D:S:ANO1ljL_qwE&gsr=CjiKAzUKGQoTODgzMDYxNTI5OTEyMjE3NjMzORAIGAMSFgoQY29tLmR1YmFpY3VsdHVyZRABGAMYAQ%3D%3D:S:ANO1ljKB_48&hl=en_IN&gl=US";
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse(url))

                }

            }
            true
        }
*//*

    }
*/

    private fun confirmationDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("Are you sure want to open phone settings?")
        alertDialog.setPositiveButton(
            "OK"
        ) { p0, p1 ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        alertDialog.setNegativeButton("No") { p0, p1 ->

        }
        alertDialog.show()

    }

    fun setSearchVisibility(isVisible: Int) {
        binding.appBar.searchView.visibility = isVisible
    }

    fun setToolBarVisibility(isVisible: Int) {
        binding.appBar.toolbar.visibility = isVisible
    }

    fun getToolbar(): Toolbar {
        return binding.appBar.toolbar
    }

    /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.main, menu)
         return true
     }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return true
    }


    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(newBase!!))
    }


    override fun onBackPressed() {
        /* if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
             binding.drawerLayout.closeDrawer(GravityCompat.START)
         } else if (homeFragment?.isNavigationLayoutVisible()!!) {
             homeFragment?.stopNavigation()
         } else {
        */     super.onBackPressed()
        //}


    }

    fun clearSearch() {
        binding.appBar.searchView.setQuery("", false)
    }

    private fun createLink(activity: Activity) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(createDynamicLink())
            .buildShortDynamicLink()
            .addOnCompleteListener(activity) { task ->

                if (task.isSuccessful) {
                    val shortLink = task.result?.shortLink
                    shareIntent(shortLink)
                }
            }
    }


    private fun shareIntent(shortLink: Uri?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.app_name) + "Share"
        )
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "${getString(R.string.share_app_text)}\n${shortLink.toString()}"
        )
        startActivity(intent)

    }


    private fun createDynamicLink(): Uri {
        val appCode = "smartguide.page.link"
        val deepLink = Uri.parse("https://www.smartguide.co.id")
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(deepLink)
            .setDynamicLinkDomain(appCode)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .setIosParameters(
                DynamicLink.IosParameters.Builder("com.dubaiculture.smartguide")
                    .setCustomScheme("dlscheme").build()
            )

            .buildDynamicLink()
        return dynamicLink.uri
    }

    @SuppressLint("MissingPermission")
    fun addGeofence() {
/*
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("TAG", "onPermissionResult: ")
                // Geofences added
                // ...
            }
            addOnFailureListener {
                Log.d("TAG", "onPermissionResult: failure $it ")
                // Failed to add geofences
                // ...
            }
        }
*/
    }

    companion object SDKMethods {
        fun setIAKEYS(context: Context, apiKey: String, secretKey: String) {
            sharePreferenceObject(context)[AppConstant.INDOOR_ATlAS_APIKEY] = apiKey
            sharePreferenceObject(context)[AppConstant.INDOOR_ATlAS_SECRETKEY] = secretKey
        }
        fun sharePreferenceObject(context: Context): SharedPreferences {
            return PreferenceHelper.defaultPrefs(context)

        }
        fun callMapbox(context: Context, token: String) {
            sharePreferenceObject(context)[AppConstant.MAPBOX_TOKEN] =token
        }

    }





}