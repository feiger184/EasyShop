package com.feicui.easyshop.main.me.persongoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.main.shop.detail.GoodsDetailActivity;
import com.feicui.easyshop.main.shop.ShopAdapter;
import com.feicui.easyshop.main.shop.ShopView;
import com.feicui.easyshop.model.GoodsInfo;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class PersonGoodsActivity extends MvpActivity<ShopView,PersonGoodsPresenter> implements ShopView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /*下拉刷新和上拉加载的控件*/
    @BindView(R.id.refreshLayout)
    PtrClassicFrameLayout ptrFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_load_error)
    TextView tv_load_error;
    @BindView(R.id.tv_load_empty)
    TextView tv_load_empty;

    @BindString(R.string.load_more_end)
    String load_more_end;

    private String pageType = "";//商品类型，空值为全部商品

    private ActivityUtils activityUtils;
    private ShopAdapter shopAdapter;//数据展示与市场页面相同，适配器一样

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_goods);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        shopAdapter = new ShopAdapter();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置toolbar的监听
        toolbar.setOnMenuItemClickListener(onMenuClickListener);
        initView();//初始化视图
    }

    private void initView() {

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        shopAdapter.setListener(new ShopAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(GoodsInfo goodsInfo) {
                 //跳转到我的商品的详情页
                Intent intent = GoodsDetailActivity.getStartIntent(PersonGoodsActivity.this, goodsInfo.getUuid(), 1);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(shopAdapter);

        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrFrameLayout.setBackgroundResource(R.color.recycler_bg);
        ptrFrameLayout.setDurationToCloseHeader(1500);

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
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

    @NonNull
    @Override
    public PersonGoodsPresenter createPresenter() {
        return new PersonGoodsPresenter();
    }


    private Toolbar.OnMenuItemClickListener onMenuClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_household:
                    presenter.refreshData("household");
                    break;
                case R.id.menu_electron:
                    presenter.refreshData("electron");
                    break;
                case R.id.menu_dress:
                    presenter.refreshData("dress");
                    break;
                case R.id.menu_book:
                    presenter.refreshData("book");
                    break;
                case R.id.menu_toy:
                    presenter.refreshData("toy");
                    break;
                case R.id.menu_gift:
                    presenter.refreshData("gift");
                    break;
                case R.id.menu_other:
                    presenter.refreshData("other");
                    break;
            }
            return false;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //设置toolbar菜单选项
        getMenuInflater().inflate(R.menu.menu_goods_type, menu);
        return true;
    }


    /*
    * 进入本页面，自动刷新
    * */
    @Override
    protected void onStart() {
        super.onStart();
        if (shopAdapter.getItemCount() == 0) {
            ptrFrameLayout.autoRefresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showRefresh() {
        tv_load_empty.setVisibility(View.GONE);
        tv_load_error.setVisibility(View.GONE);
    }

    @Override
    public void showRefreshError(String msg) {
        ptrFrameLayout.refreshComplete();
        if (shopAdapter.getItemCount() > 0) {
            activityUtils.showToast(msg);
            return;
        }
        tv_load_error.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRefreshEnd() {
        ptrFrameLayout.refreshComplete();
        tv_load_empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefresh() {
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void showLoadMoreLoading() {
        tv_load_error.setVisibility(View.GONE);

    }

    @Override
    public void showLoadMoreError(String msg) {
        ptrFrameLayout.refreshComplete();
        if (shopAdapter.getItemCount() > 0){
            activityUtils.showToast(msg);
            return;
        }
        tv_load_error.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadMoreEnd() {
        activityUtils.showToast(load_more_end);
        ptrFrameLayout.refreshComplete();
    }
    @Override
    public void hideLoadMore() {
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void addMoreData(List<GoodsInfo> data) {
        shopAdapter.addData(data);
    }

    @Override
    public void addRefreshData(List<GoodsInfo> data) {
        shopAdapter.clear();
        if (data != null)shopAdapter.addData(data);
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }
}
