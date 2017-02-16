package com.feicui.easyshop.main.detail;

import com.feicui.easyshop.model.GoodsDetail;
import com.feicui.easyshop.model.User;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.ArrayList;

/**
 * 商品详情视图接口
 */

public interface GoodsDetailView extends MvpView {

    void showProgress();

    void hideProgress();

    //设置图片路径
    void setImageData(ArrayList<String> arrayList);

    //设置商品信息
    void setData(GoodsDetail data, User goods_user);

    //*商品不存在了*/
    void showError();

    //提示信息
    void showMessage(String msg);

    //*删除商品*/
    void deleteEnd();
}
