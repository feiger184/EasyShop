package com.feicui.easyshop.user.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * 登录视图接口
 */

public interface LoginView extends MvpView {

    void showPrb();

    void hidePrb();

    void showMsg(String msg);

    void loginSuccess();

    void loginFailed();

}
