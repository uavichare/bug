package com.example.buglibrary;

import android.content.Context;
import android.content.Intent;

public class MyButton {
    MyListener ml;

    // constructor

    public MyButton(MyListener ml) {
        //Setting the listener
        this.ml = ml;
    }


    public void MyLogicToIntimateOthers(String message) {
        //Invoke the interface
        ml.s(message);
        // Toast.makeText(c,"success",Toast.LENGTH_SHORT).show();

    }

    public void OpenScreen(Context c) {
        Intent intent = null;
        try {
            intent = new Intent(c,Class.forName("com.example.buglibrary.Rocket") );
            c.startActivity(intent);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}