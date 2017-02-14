package com.feicui.easyshop.main.me.personInfo;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.File;

import okhttp3.Call;

/**
 * 个人信息业务
 */

public class PersonPersenter extends MvpNullObjectBasePresenter<PersonView>{

    private Call call;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call !=null) call.cancel();
    }

    //上传头像
    public void updataAvatar(File file){
        // TODO: 2017/2/14 0014  上传头像，网络请求
    }
}
