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

       // SDKActivity().callMapbox(this,"pk.eyJ1IjoibWVycmlsbC1hbW0iLCJhIjoiY2o4MnM0dm0wNTg5ODJ3bzNiajV1cHF2MyJ9.cLz-hmZiv5NbM4LGt-aBUA")
        InsuideSDK().authenticationOfMapbox(this,"pk.eyJ1IjoibWVycmlsbC1hbW0iLCJhIjoiY2o4MnM0dm0wNTg5ODJ3bzNiajV1cHF2MyJ9.cLz-hmZiv5NbM4LGt-aBUA")

        val intent=Intent(this, SDKActivity::class.java)
        startActivity(intent)

    }


}