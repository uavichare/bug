package com.example.buglibrary

import android.content.Context
import android.content.Intent

public class InsuideSDK {


     public fun authenticationInsuideIndoor(apiKey:String,apiSecretKey:String)
    {
        SDKActivity().setIAKEYS(apiKey,apiSecretKey)
    }
    public fun authenticationOfMapbox(token:String)
    {
        SDKActivity().callMapbox(token)
    }
   public fun OpenSdkScreen(c: Context) {
        var intent: Intent? = null
        try {
            intent = Intent(c, Class.forName("com.example.buglibrary.SDKActivity"))
            c.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }


}