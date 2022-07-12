package com.example.buglibrary.ui.about_us

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buglibrary.R
import javax.inject.Inject

class AboutUsViewModel @Inject constructor() :
    ViewModel() {

    fun prepareList(context: Context)= MutableLiveData<List<Home>>().apply {
        val arrayList = ArrayList<Home>()
        arrayList.add(Home(Component.ABOUT_THE_APP))
        arrayList.add(Home(Component.POLICIES, policyList(context)))
        arrayList.add(Home(Component.LIBRARY_WE_USE))
        value = arrayList
    }

    private fun policyList(context: Context): List<PrivacyModel> {
        val list: MutableList<PrivacyModel> = ArrayList()
        list.add(PrivacyModel(context.getString(R.string.private_policies), context.getString(R.string.click_privacy_policy)))
        list.add(PrivacyModel(context.getString(R.string.term_condition), context.getString(R.string.click_term_condition)))
        return list

    }


}
