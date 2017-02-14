package com.feicui.easyshop.user.register;

import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.User;
import com.feicui.easyshop.model.UserResult;
import com.feicui.easyshop.network.EasyShopClient;
import com.feicui.easyshop.network.UICallBack;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.IOException;

import okhttp3.Call;

/**
 * 注册界面业务处理
 */

public class RegisterPresenter extends MvpNullObjectBasePresenter<RegisterView> {

    private Call call;

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

                    //执行注册成功的方法
                    getView().registerSuccess();

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


    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call != null) {
            call.cancel();

        }
    }
}
