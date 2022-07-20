package com.example.buglibrary;

import android.content.Context;
import android.content.Intent;


public class IndoorLibrary {



    public static void getNavFragment(Context c)
    {
        Intent intent = null;
        try {
            intent = new Intent(c,Class.forName("com.example.buglibrary.SDKActivity") );
            c.startActivity(intent);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
