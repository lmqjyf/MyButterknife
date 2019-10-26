package com.bitcoin.juwan.mybutterknife;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.applibrary.ActivityViewFinder;
import com.bitcoin.juwan.applibrary.FragmentViewFinder;
import com.bitcoin.juwan.applibrary.ViewBinder;
import com.bitcoin.juwan.applibrary.ViewFinder;

import java.util.HashMap;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description :
 */
public class MyButterKnife {

    private static final  ActivityViewFinder activityViewFinder = new ActivityViewFinder();


    private static final FragmentViewFinder fragmentViewFinder = new FragmentViewFinder();

    public static final HashMap<String, ViewBinder> map = new HashMap<>();

    /**
     * Activity
     * @param activity
     */
    public static void init(Activity activity) {
        init(activity, activity, activityViewFinder);
    }

    /**
     * Fragment
     * @param fragment
     * @param view
     */
    public static void init(Fragment fragment, View view) {
        init(fragment, view, fragmentViewFinder);
    }

    public static void init(Object host, Object o, ViewFinder viewFinder) {
        String hostName = host.getClass().getName();
        ViewBinder hostViewBinder = map.get(hostName);
        if(hostViewBinder == null) {
            try {
                Class<?> aClass = Class.forName(hostName + "$ViewBinder");
                hostViewBinder = (ViewBinder) aClass.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            map.put(hostName, hostViewBinder);
        }
        //绑定View
        hostViewBinder.bindView(host, o, viewFinder);
    }

    public static void unBind(Activity host) {
        String hostName = host.getClass().getName();
        ViewBinder viewBinder = map.get(hostName);
        viewBinder.unBindView(host);
        map.remove(hostName);
    }
}
