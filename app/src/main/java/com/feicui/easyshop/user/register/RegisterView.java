package com.feicui.easyshop.user.register;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * 注册视图接口
 */

public interface RegisterView extends MvpView {

    void showPrb();

    void hidePrb();

    void showMsg(String msg);

    void registerSuccess();

    void registerFailed();

    void showUserPasswordError(String msg);

}
