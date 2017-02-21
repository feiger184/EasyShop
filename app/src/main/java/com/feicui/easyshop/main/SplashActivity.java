package com.feicui.easyshop.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.commons.CurrentUser;
import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.User;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.hyphenate.easeui.domain.EaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activityUtils = new ActivityUtils(this);

        EventBus.getDefault().register(this);

        //  环信登录相关（账号冲突踢出）

        if (getIntent().getBooleanExtra("AUTO_LOGIN", false)) {
            //清除本地缓存的用户信息
            CachePreferences.clearAllData();
            //踢出时，登出环信
            HxUserManager.getInstance().asyncLogout();
        }
        // 判断用户是否登录，自动登录
        if (CachePreferences.getUser().getName() != null && !HxUserManager.getInstance().isLogin()) {
            User user = CachePreferences.getUser();
            EaseUser easeUser = CurrentUser.convert(user);
            HxUserManager.getInstance().asyncLogin(easeUser, user.getPassword());
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                activityUtils.startActivity(MainActivity.class);
                finish();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent simpleEvent) {
        if (simpleEvent.type!= HxEventType.LOGIN) return;
        activityUtils.startActivity(MainActivity.class);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {
        if (event.type!=HxEventType.LOGIN) return;
        activityUtils.startActivity(MainActivity.class);
        throw new RuntimeException("login error");
    }

}

