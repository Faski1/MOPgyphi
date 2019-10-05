package com.example.mopgyphi.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {
    public static Gson build() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("dd-MM-yyyy'T'HH:mm:ss");
        return builder.create();
    }
}
