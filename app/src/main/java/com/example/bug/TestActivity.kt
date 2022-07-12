package com.example.bug

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dubaiculture.smartguide.MainActivity
import com.google.firebase.FirebaseApp

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)

    }
}