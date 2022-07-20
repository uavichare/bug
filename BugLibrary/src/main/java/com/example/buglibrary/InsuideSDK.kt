package com.example.buglibrary

import android.content.Context
import android.content.Intent

class InsuideSDK {


    fun authenticationInsuideIndoor(apiKey:String,apiSecretKey:String)
    {
        SDKActivity().setIAKEYS(apiKey,apiSecretKey)
    }
    fun authenticationOfMapbox(token:String)
    {
        SDKActivity().callMapbox(token)
    }
    fun OpenSdkScreen(c: Context, apiKey: String?, secretKey: String?, accessToken: String?) {
        var intent: Intent? = null
        try {
            intent = Intent(c, Class.forName("com.example.buglibrary.SDKActivity"))
            c.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }


}