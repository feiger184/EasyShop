package com.feicui.easyshop;

import android.app.Application;

import com.feicui.easyshop.model.CachePreferences;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class EasyShopApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CachePreferences.init(this);
    }
}
