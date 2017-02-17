package com.feicui.easyshop.main.me.goodsupload;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * 商品上传视图接口
 */

public interface GoodsUpLoadView extends MvpView {

    void showPrb();
    void hidePrb();
    void upLoadSuccess();
    void showMsg(String msg);
}
