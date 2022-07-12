package com.example.buglibrary.utils.ext

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.buglibrary.R

/**
 * Kotlin extensions for dependency injection
 */

inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, factory)[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.injectViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, factory)[T::class.java]
}
fun Fragment.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    if (mayNavigate()) findNavController().navigate(directions, navOptions)
}
fun Fragment.mayNavigate(): Boolean {

    val navController = findNavController()
    val destinationIdInNavController = navController.currentDestination?.id

    // add tag_navigation_destination_id to your ids.xml so that it's unique:
    val destinationIdOfThisFragment = view?.getTag(R.id.poiDetailFragment) ?: destinationIdInNavController

    // check that the navigation graph is still in 'this' fragment, if not then the app already navigated:
    if (destinationIdInNavController == destinationIdOfThisFragment) {
        view?.setTag(R.id.poiDetailFragment, destinationIdOfThisFragment)
        return true
    } else {
        Log.d("FragmentExtensions", "May not navigate: current destination is not the current fragment.")
        return false
    }
}