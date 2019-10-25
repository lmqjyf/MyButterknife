package com.bitcoin.juwan.applibrary;

import android.app.Activity;

/**
 * FileName：ViewBinder
 * Create By：liumengqiang
 * Description：TODO
 */
public interface ViewBinder<T> {
    void bindView(T host, Object o, ViewFinder viewFinder);

    void unBindView(T host);
}
