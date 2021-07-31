/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
  */
package ghasemi.abbas.wordpress.api;

import org.json.JSONArray;

public interface ResultListener {
    void onResult(boolean isSuccess, String msg, JSONArray jsonArray,int id);
}
