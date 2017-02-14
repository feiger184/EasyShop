package com.feicui.easyshop.user.login;

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
 * Created by Administrator on 2017/2/14 0014.
 */

public class LoginPresenter extends MvpNullObjectBasePresenter<LoginView> {


    private Call call;

    public void login(String username, String password) {
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

                if (result.getCode()==1) {
                    User user = result.getData();
                    CachePreferences.setUser(user);
                    getView().loginSuccess();
                    getView().showMsg("登录成功");
                } else if (result.getCode() == 2) {
                    getView().hidePrb();
                    getView().showMsg(result.getMessage());
                    getView().loginFailed();

                }else {
                    getView().hidePrb();
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
