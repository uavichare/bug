package com.example.buglibrary;

import android.content.Context;
import android.content.Intent;


public class IndoorLibrary {

    public static void s(Context c, String message){
       // Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
       // Parent p = new Parent();
        Parent.s(c,message);


    }


    public static void getNavFragment(Context c)
    {
        Intent intent = null;
        try {
            intent = new Intent(c,Class.forName("com.example.buglibrary.MainActivity") );
            c.startActivity(intent);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
