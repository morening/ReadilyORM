package com.morening.readilyorm.util;

import android.util.Log;

import com.morening.readilyorm.BuildConfig;
import com.morening.readilyorm.ReadilyORM;

/**
 * Created by morening on 2018/9/2.
 */
final public class Logger {

    public static void d(Object target, String message){
        if (BuildConfig.DEBUG){
            Log.d(target.getClass().getSimpleName(), message);
        }
    }
}
