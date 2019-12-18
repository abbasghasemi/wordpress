/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.builder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PullRefreshLayout extends com.baoyz.widget.PullRefreshLayout {

    private boolean loading = true;

    public PullRefreshLayout(Context context) {
        super(context);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (loading) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (loading) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
    }
}
