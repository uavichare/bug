package com.example.buglibrary

import android.content.Context
import android.content.Intent

 class InsuideSDK {

     companion object SDKMethods {
         fun authenticationInsuideIndoor(context: Context, apiKey: String, apiSecretKey: String) {
             SDKActivity.setIAKEYS(context, apiKey, apiSecretKey)
         }

         fun authenticationOfMapbox(context: Context, token: String) {
             SDKActivity.callMapbox(context, token)
         }

         fun OpenSdkScreen(c: Context) {
             var intent: Intent? = null
             try {
                 intent = Intent(c, Class.forName("com.example.buglibrary.SDKActivity"))
                 c.startActivity(intent)
             } catch (e: ClassNotFoundException) {
                 e.printStackTrace()
             }
         }
     }

}