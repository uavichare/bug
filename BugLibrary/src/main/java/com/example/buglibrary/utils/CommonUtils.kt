package com.example.buglibrary.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.get
import com.example.buglibrary.helper.PreferenceHelper.set
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Created by Furqan on 21-03-2018.
 */
class CommonUtils {
    companion object {

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun pxFromDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun getDistanceFromLatLonInMeters(
            lat1: Double,
            lon1: Double,
            lat2: Double,
            lon2: Double
        ): Double {
            val R = 6371.0 // Radius of the earth in km
            val dLat = deg2rad(lat2 - lat1)  // deg2rad below
            val dLon = deg2rad(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(lat1)) * cos(
                deg2rad(lat2)
            ) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = R * c // Distance in km
            return d * 1000
        }

        private fun deg2rad(deg: Double): Double {
            return deg * (Math.PI / 180)
        }


        fun hideKeyboard(context: Context, view: View) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun isValidEmail(target: CharSequence): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }

           fun getDeviceId(context: Context): String {
               val pref = PreferenceHelper.defaultPrefs(context)
               val uniqueUserID: String? = pref[AppConstant.UNIQUE_USER_ID]
               return uniqueUserID?.let {
                   it
               } ?: kotlin.run {
                   val uniqueId = Settings.Secure.getString(
                       context.contentResolver,
                       Settings.Secure.ANDROID_ID
                   )
                   pref[AppConstant.UNIQUE_USER_ID] = uniqueId
                   uniqueId
               }
           }

        fun generate(view: View): Bitmap? {
            val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(measureSpec, measureSpec)
            val measuredWidth = view.measuredWidth
            val measuredHeight = view.measuredHeight
            view.layout(0, 0, measuredWidth, measuredHeight)
            val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }
        /*  fun getUser(context: Context): User? {
              val pref = PreferenceHelper.defaultPrefs(context)
              val isLogin: Boolean? = pref[AppConstant.IS_LOGIN]
              if (isLogin!!) {
                  val loginDetail: String? = pref[AppConstant.TOKEN_LOGIN_DETAIL]
                  val type = object : TypeToken<LoginEncryptedResponse>() {}.type
                  val loginResponse = Gson().fromJson<LoginEncryptedResponse>(loginDetail, type)
                  if (loginResponse != null) {
                      return loginResponse.doc
                  }
              }
              return null
          }*/

        /* fun singleChoiceDialog(context: Context, item: Array<String>) {
             if (item.size > 1) {
                 AlertDialog.Builder(context)
                     .setSingleChoiceItems(
                         item, 0
                     ) { p0, p1 ->
                         IntentUtils.dialPhoneNumber(context, item[p1])
                         p0.dismiss()
                     }
                     .setNegativeButton(
                         "Cancel"
                     ) { p0, p1 -> p0.dismiss() }
                     .show()
             } else {
                 IntentUtils.dialPhoneNumber(
                     context,
                     item[0]
                 )
             }

         }*/


    }
}