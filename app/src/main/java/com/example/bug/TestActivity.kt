package com.example.bug

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.buglibrary.InsuideSDK
import com.example.buglibrary.SDKActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        InsuideSDK.authenticationOfMapbox(this,"pk.eyJ1IjoibWVycmlsbC1hbW0iLCJhIjoiY2o4MnM0dm0wNTg5ODJ3bzNiajV1cHF2MyJ9.cLz-hmZiv5NbM4LGt-aBUA")
        InsuideSDK.OpenSdkScreen(this)
        InsuideSDK.authenticationInsuideIndoor(this,"675933d8-45d2-4397-aef6-80bcf5861fed","rxNJoW/xt1iVy3BA5c0r69tjf6097VxsW9dz56JzOnQsRbcD3qGDyKT0e3iA1XGEpn2N5JHL7FpgZSyuF5BKSXXWqJ+Y2nWqr8lXa5lmECBYOxiZzXnCih5Ozljgag==")


    }


}