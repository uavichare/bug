package com.example.buglibrary.ui.contactus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buglibrary.data.ContactMenu
import com.example.buglibrary.helper.AppConstant
import java.util.*
import kotlin.collections.set

class ContactUsViewModel() : ViewModel() {

    //    private val contactList = MutableLiveData<ArrayList<NavigationItemDataModel>>()
    val contactMenuList = MutableLiveData<List<ContactMenu>>()

    fun fetchContact() /*:MutableLiveData<ArrayList<NavigationItemDataModel>> */ {
        val hashMapRequest = HashMap<String, String>()
        hashMapRequest["appname"] = AppConstant.APP_NAME
        hashMapRequest["masterParentId"] = "1"
        hashMapRequest["parentId"] = "1"
//        hashMapRequest["token"] = app.getString(R.string.app_token)
        /* val apiContent = ApiManager.client(ApiContent::class.java)
         apiContent.fetchContactUs(hashMapRequest)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : DisposableObserver<BudDataResponse<ContactMenu>>() {
                 override fun onComplete() {

                 }

                 override fun onNext(t: BudDataResponse<ContactMenu>) {

                     if (t.success) {
                         contactMenuList.postValue(t.menulist)
                     }

                 }

                 override fun onError(e: Throwable) {
                     Log.e("TAG", "onError: ", e)
                 }
             })*/


//        contactList.value = contactList()

//        return contactList
    }


    /*  private fun contactList(): ArrayList<NavigationItemDataModel> {
          val contactItemList = ArrayList<NavigationItemDataModel>()
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.call_center),
                  R.drawable.ic_call_unfilled, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.Email_Id),
                  R.drawable.ic_mail, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(

                  getApplication<Application>().getString(R.string.facebook),
                  R.drawable.ic_facebook_unfilled_icon, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.instagram),
                  R.drawable.ic_instagram_unfilled_ion, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.twitter),
                  R.drawable.ic_twitter_unfilled_icon, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.news_letter),
                  R.drawable.ic_file_text, 1
              )
          )
          contactItemList.add(
              NavigationItemDataModel(
                  getApplication<Application>().getString(R.string.news_letter_sign_up),
                  R.drawable.ic_news_letter_sign_up, 1
              )
          )

          return contactItemList
      }*/

}
