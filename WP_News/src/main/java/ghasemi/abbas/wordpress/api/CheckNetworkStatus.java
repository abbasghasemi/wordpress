/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
  */
package ghasemi.abbas.wordpress.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ghasemi.abbas.wordpress.Application;

public class CheckNetworkStatus {

    public static boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager) Application.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        boolean wifi = false, network = false;
        NetworkInfo _network = connectivityManager.getActiveNetworkInfo();
        if (_network != null) {
            network = _network.isConnectedOrConnecting();

        }
        NetworkInfo _wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (_wifi != null) {
            wifi = _wifi.isConnectedOrConnecting();
        }
        NetworkInfo _mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (_mobile != null) {
            network = _mobile.isConnectedOrConnecting();
        }
        return network || wifi;
    }

}