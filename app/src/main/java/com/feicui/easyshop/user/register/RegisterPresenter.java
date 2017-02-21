package com.feicui.easyshop.user.register;

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
 * 注册界面业务处理
 */

public class RegisterPresenter extends MvpNullObjectBasePresenter<RegisterView> {

    private Call call;

    private String hxPassword;//环信相关

    @Override
    public void attachView(RegisterView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    public void register(String username, String password) {

        getView().showPrb();
        call = EasyShopClient.getInstance().Register(username, password);
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {

                getView().hidePrb();
                getView().showMsg(e.getMessage());

            }

            @Override
            public void onResponseUI(Call call, String body) {
                getView().hidePrb();
                UserResult result = new Gson().fromJson(body, UserResult.class);

                //根据不同的结果码处理
                if (result.getCode() == 1) {
                    //成功提示
                    getView().showMsg("注册成功");
                    //拿到用户的实体类
                    User user = result.getData();
                    //将用户的信息保存到本地配置中
                    CachePreferences.setUser(user);

//                    //执行注册成功的方法(加入环信模块前）
//                    getView().registerSuccess();

                    //加入环信模块后
                    EaseUser easeUser = CurrentUser.convert(user);//转换为环信的实体
                    HxUserManager.getInstance().asyncLogin(easeUser, hxPassword);

                } else if (result.getCode() == 2) {
                    //提示失败信息
                    getView().showMsg(result.getMessage());
                    //执行注册失败的方法
                    getView().registerFailed();
                } else {
                    getView().showMsg("未知错误！");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxSimpleEvent event) {
        if (event.type != HxEventType.LOGIN) return;
        hxPassword = null;
        getView().registerSuccess();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HxErrorEvent event) {
        //判断是否是注册成功事件
        if (event.type != HxEventType.LOGIN) return;

        hxPassword = null;
        getView().hidePrb();
        getView().showMsg(event.toString());
    }

    //视图销毁，取消网络请求
    @Override
    public void detachView(boolean retainInstance) {

        super.detachView(retainInstance);
        if (call != null) call.cancel();
        EventBus.getDefault().unregister(this);
    }
}
