package com.example.buglibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dubaiculture.smartguide.MainActivity;


public class Rocket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket);
        Toast.makeText(this, "done gopu", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this, MainActivity.class);
        startActivity(i);
    }
}