package com.feicui.easyshop.user.login;

import com.feicui.easyshop.commons.CurrentUser;
import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.User;
import com.feicui.easyshop.model.UserResult;
import com.feicui.easyshop.network.EasyShopClient;
import com.feicui.easyshop.network.UICallBack;
import com.feicuiedu.apphx.model.HxUserManager;
import com.feicuiedu.apphx.model.event.HxErrorEvent;
import com.feicuiedu.apphx.model.event.HxEventType;
import com.feicuiedu.apphx.model.event.HxSimpleEvent;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hyphenate.easeui.domain.EaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;

/**
 * 登录业务类
 */

public class LoginPresenter extends MvpNullObjectBasePresenter<LoginView> {

    private String hxPassword;
    private Call call;

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    public void login(String username, String password) {
        hxPassword = password;
        getView().showPrb();
        call = EasyShopClient.getInstance().login(username, password);
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().hidePrb();
                getView().showMsg(e.getMessage());

            }

            @Override
            public void onResponseUI(Call call, String body) {

                UserResult result = new Gson().fromJson(body, UserResult.class);

                if (result.getCode() == 1) {
                    User user = result.getData();
                    CachePreferences.setUser(user);
//                    getView().loginSuccess();
//                    getView().showMsg("登录成功");加入环信模块前

                    EaseUser easeUser = CurrentUser.convert(user);
                    HxUserManager.getInstance().asyncLogin(easeUser, hxPassword);
                } else if (result.getCode() == 2) {
                    getView().hidePrb();
                    getView().showMsg(result.getMessage());
                    getView().loginFailed();

                } else {
                    getView().hidePrb();
                    getView().showMsg("未知错误！");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent event) {
        //判断是否是登录成功事件
        if (event.type != HxEventType.LOGIN) return;

        hxPassword = null;
        //调用登录成功的方法
        getView().loginSuccess();
        getView().showMsg("登录成功");

        EventBus.getDefault().post(new UserResult());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {
        //判断是否是登录成功事件
        if (event.type != HxEventType.LOGIN) return;

        hxPassword = null;
        getView().hidePrb();
        getView().showMsg(event.toString());
    }


    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call != null) call.cancel();

        EventBus.getDefault().unregister(this);
    }
}
