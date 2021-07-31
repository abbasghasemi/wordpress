package ghasemi.abbas.wordpress.api;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class KeepRequestQueue {

    private RequestQueue requestQueue;
    @SuppressLint("StaticFieldLeak")
    private static KeepRequestQueue keepRequestQueue;
    private Context context;

    private KeepRequestQueue(Context context) {
        this.context = context;
    }

    public static synchronized KeepRequestQueue newKeepRequestQueue(Context context) {
        if (keepRequestQueue == null) {
            keepRequestQueue = new KeepRequestQueue(context);
        }
        return keepRequestQueue;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> Request<T> add(Request<T> request) {
        return getRequestQueue().add(request);
    }
}
