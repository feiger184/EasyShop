package com.feicui.easyshop.main.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.model.GoodsInfo;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 市场界面
 */

public class ShopFragment extends MvpFragment<ShopView, ShopPresenter> implements ShopView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;//展示商品的列表
    @BindView(R.id.refreshLayout)
    PtrClassicFrameLayout refreshLayout;//刷新加载的控件
    @BindView(R.id.tv_load_error)
    TextView tvLoadError;//错误提示

    private View view;
    private ActivityUtils activityUtils;
    private ShopAdapter shopAdapter;

    //获取商品时，商品类型，获取全部商品时为空
    private String pageType = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        shopAdapter = new ShopAdapter();
    }

    @NonNull
    @Override
    public ShopPresenter createPresenter() {
        return new ShopPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shop, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        shopAdapter.setListener(new ShopAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(GoodsInfo goodsInfo) {
                // TODO: 2017/2/16 0016 点击跳转到商品详情页面
            }
        });
        recyclerView.setAdapter(shopAdapter);

        //初始化refreshlayouot
        refreshLayout.setLastUpdateTimeRelateObject(this);
        refreshLayout.setBackgroundResource(R.color.recycler_bg);
        refreshLayout.setDurationToCloseHeader(1500);
        refreshLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                presenter.loadData(pageType);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.refreshData(pageType);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (shopAdapter.getItemCount() == 0) {
            refreshLayout.autoRefresh();
        }
    }

    @OnClick(R.id.tv_load_error)
    public void click() {
        refreshLayout.autoRefresh();

    }


    @Override
    public void showRefresh() {
        tvLoadError.setVisibility(View.GONE);
    }

    @Override
    public void showRefreshError(String msg) {

        refreshLayout.refreshComplete();
        if (shopAdapter.getItemCount() > 0) {
            activityUtils.showToast(msg);
            return;
        }
        tvLoadError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRefreshEnd() {
        activityUtils.showToast(getResources().getString(R.string.refresh_more_end));
        refreshLayout.refreshComplete();
    }

    @Override
    public void hideRefresh() {
        refreshLayout.refreshComplete();
    }

    @Override
    public void showLoadMoreLoading() {
        tvLoadError.setVisibility(View.GONE);
    }

    @Override
    public void showLoadMoreError(String msg) {

        refreshLayout.refreshComplete();
        if (shopAdapter.getItemCount() > 0) {
            activityUtils.showToast(msg);
            return;
        }
        tvLoadError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadMoreEnd() {
        activityUtils.showToast(getResources().getString(R.string.load_more_end));
        refreshLayout.refreshComplete();
    }

    @Override
    public void hideLoadMore() {
        refreshLayout.refreshComplete();
    }

    @Override
    public void addMoreData(List<GoodsInfo> data) {

        shopAdapter.addData(data);

    }

    @Override
    public void addRefreshData(List<GoodsInfo> data) {
        shopAdapter.clear();
        if (data != null) {
            shopAdapter.addData(data);
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }
}
