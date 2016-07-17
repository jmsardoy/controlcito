package com.example.juanso.control_app;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ConnectionManager {
    private static RequestQueue sQueue;

    public static RequestQueue getInstance(Context context){
        if(sQueue == null){
            sQueue = Volley.newRequestQueue(context);
        }
        return sQueue;
    }
}
