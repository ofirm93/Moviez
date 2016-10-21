package com.example.android.moviez;

import android.content.SharedPreferences;

import static java.lang.reflect.Array.get;
import static java.lang.reflect.Array.getInt;


/**
 * Created by Ofir on 21/10/2016.
 */

public class Utility {
    private final static String PAGES = "PAGES";
    public final static String LAST_GENRES_UPDATE = "last_genres_update";

    public static long getLastUpdate(SharedPreferences sharedPreferences){
        return sharedPreferences.getLong(LAST_GENRES_UPDATE, -1);
    }

    public static void addPage(SharedPreferences sharedPreferences){
        int page = sharedPreferences.getInt(PAGES, -1);
        if(page != -1){
            sharedPreferences.edit().putInt(PAGES, page+1).commit();
        }
    }

    public static void resetPages(SharedPreferences sharedPreferences){
        sharedPreferences.edit().putInt(PAGES, 1).commit();

    }

    public static int getPages(SharedPreferences sharedPreferences){
        return sharedPreferences.getInt(PAGES, -1);
    }

    public static void setLastUpdateNowLoading(SharedPreferences sharedPreferences){
        sharedPreferences.edit().putLong(LAST_GENRES_UPDATE, -1).commit();
    }

    public static void updateLastUpdate(SharedPreferences sharedPreferences) {
        long currentTime = System.currentTimeMillis();
        sharedPreferences.edit().putLong(LAST_GENRES_UPDATE, currentTime).commit();
    }
}
