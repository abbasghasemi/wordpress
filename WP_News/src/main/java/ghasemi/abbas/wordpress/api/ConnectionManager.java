package ghasemi.abbas.wordpress.api;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import ghasemi.abbas.wordpress.ui.MainActivity;
import ghasemi.abbas.wordpress.BuildApp;

public class ConnectionManager {

    private String url;
    private String params;
    private int timeOut = BuildApp.timeout * 1000;

    private ConnectionManager() {
    }

    public static class Builder {
        private ConnectionManager connectionManager;

        public Builder() {
            connectionManager = new ConnectionManager();
        }

        public Builder setURL(String url) {
            connectionManager.url = url;
            return this;
        }

        public Builder addParams(String key, Object value) {
            if (TextUtils.isEmpty(connectionManager.params)) {
                connectionManager.params = "?" + key + "=" + connectionManager.encodeUrl(value.toString());
            } else {
                connectionManager.params += "&" + key + "=" + connectionManager.encodeUrl(value.toString());
            }
            return this;
        }

        public void request(ResultConnection resultConnection) {
            if (!TextUtils.isEmpty(connectionManager.params)) {
                connectionManager.url += connectionManager.params;
            }
            connectionManager.stringRequest(resultConnection);
        }
    }

    private void stringRequest(ResultConnection resultConnection) {
        StringRequest stringRequest = new StringRequest(0, url, response -> MainActivity.activity.runOnUiThread(() -> {
            try {
                String object = response.trim();
                if (object.startsWith("{")) {
                    object = "[" + object + "]";
                }
                resultConnection.onSuccess(new JSONArray(object));
            } catch (JSONException e) {
                resultConnection.onFail(e.toString());
            }
        }), error -> MainActivity.activity.runOnUiThread(() -> resultConnection.onFail(error.toString())));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        KeepRequestQueue.newKeepRequestQueue(MainActivity.activity.getApplicationContext()).add(stringRequest);
    }


    private String encodeUrl(String data) {
        try {
            return URLEncoder.encode(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public interface ResultConnection {
        void onSuccess(JSONArray jsonArray) throws JSONException;

        void onFail(String error);
    }

}