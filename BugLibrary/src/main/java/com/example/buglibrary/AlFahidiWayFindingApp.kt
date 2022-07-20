package com.example.buglibrary

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.facebook.FacebookSdk
import com.example.buglibrary.data.Poi
import com.example.buglibrary.di.DaggerAppComponent
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.ui.home.MapViewModel
import com.mapbox.mapboxsdk.Mapbox
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class AlFahidiWayFindingApp : Application(), HasAndroidInjector,
    Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    var poi: Poi? = null
    lateinit var mapViewModel: MapViewModel
    override fun onCreate() {
        super.onCreate()


        DaggerAppComponent.builder().application(this)
            .build().inject(this)
      //  Mapbox.getInstance(this,getString(R.string.mapbox_access_token))

        registerActivityLifecycleCallbacks(this)

        FacebookSdk.sdkInitialize(applicationContext);
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        handleActivity(p0)
    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasAndroidInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(
                            fm: FragmentManager,
                            f: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if (f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    }, true
                )
        }
    }

    override fun androidInjector() = dispatchingAndroidInjector

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}