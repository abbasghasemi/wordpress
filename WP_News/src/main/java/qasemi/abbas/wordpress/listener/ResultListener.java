/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.listener;

import org.json.JSONArray;

public interface ResultListener {
    void onResult(boolean isSuccess, String msg, JSONArray jsonArray, int pages, int id);
}
