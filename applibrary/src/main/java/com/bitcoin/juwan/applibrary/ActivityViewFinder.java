package com.bitcoin.juwan.applibrary;

import android.app.Activity;
import android.view.View;

/**
 * FileName：ActivityViewFinder
 * Create By：liumengqiang
 * Description：TODO
 */
public class ActivityViewFinder implements ViewFinder {

    @Override
    public View findView(Object o, int resId) {
        return ((Activity)o).findViewById(resId);
    }
}
