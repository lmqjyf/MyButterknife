package com.bitcoin.juwan.applibrary;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description :
 */
public class FragmentViewFinder implements ViewFinder {
    @Override
    public View findView(Object o, int resId) {
        return ((View)o).findViewById(resId);
    }
}
