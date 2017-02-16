package com.feicui.easyshop.main.shop;

import com.feicui.easyshop.model.GoodsResult;
import com.feicui.easyshop.network.EasyShopClient;
import com.feicui.easyshop.network.UICallBack;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.IOException;

import okhttp3.Call;

/**
 * 所有商品业务处理
 */

public class ShopPresenter extends MvpNullObjectBasePresenter<ShopView> {


    private Call call;
    private int pageInt = 1;

    //刷新数据
    public void refreshData(String type) {
        getView().showRefresh();

        //刷新永远获得第一页数据
        call = EasyShopClient.getInstance().getGoods(1, type);
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().showRefreshError(e.getMessage());
            }

            @Override
            public void onResponseUI(Call call, String body) {
                GoodsResult goodsResult = new Gson().fromJson(body, GoodsResult.class);
                switch (goodsResult.getCode()) {
                    case 1:
                        if (goodsResult.getDatas().size() == 0) {
                            getView().showRefreshEnd();
                        } else {
                            getView().addRefreshData(goodsResult.getDatas());
                            getView().showRefreshEnd();
                        }
                        pageInt=2;
                        break;
                    default:
                        getView().showRefreshError(goodsResult.getMessage());
                        break;
                }
            }
        });

    }


    public void loadData(String type) {

        getView().showLoadMoreLoading();
        call = EasyShopClient.getInstance().getGoods(pageInt, type);
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().showLoadMoreError(e.getMessage());
            }

            @Override
            public void onResponseUI(Call call, String body) {
                GoodsResult goodsResult = new Gson().fromJson(body, GoodsResult.class);
                switch (goodsResult.getCode()) {
                    case 1:
                        if (goodsResult.getDatas().size() == 0) {
                            getView().showLoadMoreEnd();
                        } else {
                            getView().addMoreData(goodsResult.getDatas());
                            getView().showLoadMoreEnd();
                        }
                        pageInt++;
                        break;
                    default:
                        getView().showLoadMoreError(goodsResult.getMessage());
                        break;
                }
            }
        });
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call!=null) call.cancel();
    }
}
