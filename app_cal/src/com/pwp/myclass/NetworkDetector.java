package com.pwp.myclass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 大源 on 2015/8/1.
 */
public class NetworkDetector {
        public static boolean detect(Context act) {

            ConnectivityManager manager = (ConnectivityManager) act.getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            }
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();

            if (networkinfo == null || !networkinfo.isAvailable()) {
                return false;
            }
            return true;
        }


}

