package com.example.buglibrary;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Parent {
    public static void s(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }


    public static void getNavFragment(Context c)
    {
        Intent intent = null;
        try {
            intent = new Intent(c,Class.forName("com.example.fragmentTest") );
            c.startActivity(intent);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
