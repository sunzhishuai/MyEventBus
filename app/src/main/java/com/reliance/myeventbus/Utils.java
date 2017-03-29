package com.reliance.myeventbus;

import android.os.Looper;

/**
 * Created by sunz
 * E-mail itzhishuaisun@sina.com
 */

public class Utils {


    public static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return myLooper == mainLooper;
    }

}
