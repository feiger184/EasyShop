package com.feicui.easyshop.main.shop.detail;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.commons.CurrentUser;
import com.feicui.easyshop.components.AvatarLoadOptions;
import com.feicui.easyshop.components.ProgressDialogFragment;
import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.GoodsDetail;
import com.feicui.easyshop.model.User;
import com.feicui.easyshop.network.EasyShopApi;
import com.feicui.easyshop.user.login.LoginActivity;
import com.feicuiedu.apphx.model.repository.DefaultLocalUsersRepo;
import com.feicuiedu.apphx.presentation.chat.HxChatActivity;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class GoodsDetailActivity extends MvpActivity<GoodsDetailView, GoodsDetailPresenter> implements GoodsDetailView {

    private static final String UUID = "uuid";
    //从不同的页面进入详情页的状态值，0=从市场页面，1=从我的商品页面
    private static final String STATE = "state";

    public static Intent getStartIntent(Context context, String uuid, int state) {
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        intent.putExtra(UUID, uuid);
        intent.putExtra(STATE, state);
        return intent;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    /*使用开源库CircleIndicator来实现ViewPager的圆点指示器。*/
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.tv_detail_name)
    TextView tv_detail_name;
    @BindView(R.id.tv_detail_price)
    TextView tv_detail_price;
    @BindView(R.id.tv_detail_master)
    TextView tv_detail_master;
    @BindView(R.id.tv_detail_describe)
    TextView tv_detail_describe;
    @BindView(R.id.tv_goods_delete)
    TextView tv_goods_delete;
    @BindView(R.id.tv_goods_error)
    TextView tv_goods_error;
    @BindView(R.id.btn_detail_message)
    Button btn_detail_message;

    private String str_uuid;
    private ArrayList<ImageView> list;
    private ArrayList<String> list_uri;//存放图片路径的集合
    private GoodsDetailAdapter adapter;//viewPager的适配器
    private ActivityUtils activityUtils;

    private User goods_user;
    private ProgressDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityUtils = new ActivityUtils(this);
        list = new ArrayList<>();
        list_uri = new ArrayList<>();
        adapter = new GoodsDetailAdapter(list);
        adapter.setListener(new GoodsDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemListener() {
                //  图片详情
                Intent intent = GoodsDetailInfoActivity.getStartIntent(GoodsDetailActivity.this, list_uri);
                startActivity(intent);

            }
        });
        viewPager.setAdapter(adapter);
        init();//初始化视图
    }

    //初始化视图
    private void init() {
        //拿到uuid
        str_uuid = getIntent().getStringExtra(UUID);
        //从不同的页面进入详情页的状态值，0=从市场页面，1=从我的商品页面
        int btn_show = getIntent().getIntExtra(STATE, 0);
        if (btn_show == 1) {
            tv_goods_delete.setVisibility(View.VISIBLE);
            btn_detail_message.setVisibility(View.GONE);
        }
        presenter.getData(str_uuid);

    }


    @OnClick({R.id.btn_detail_message, R.id.tv_goods_delete})
    public void click(View view) {
        //判断登录状态
        if (CachePreferences.getUser().getName() == null) {
            activityUtils.startActivity(LoginActivity.class);
            return;
        }
        switch (view.getId()) {
            case R.id.btn_detail_message:
                // 跳转到环信发消息页面
                //根据环信ID判断商品归属，自己不能给自己发消息
                if (goods_user.getHx_Id().equals(CachePreferences.getUser().getHx_Id())) {
                    activityUtils.showToast("这个商品是自己发布的哦！");
                    return;
                }
                //跳转到环信的消息页面
                DefaultLocalUsersRepo.getInstance(this).save(CurrentUser.convert(goods_user));
                startActivity(HxChatActivity.getStartIntent(GoodsDetailActivity.this, goods_user.getHx_Id()));
                break;
            case R.id.tv_goods_delete:
                //删除相关
                //删除相关
                //弹一个警告，是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.goods_title_delete);
                builder.setMessage(R.string.goods_info_delete);
                //设置确认按钮，点击删除
                builder.setPositiveButton(R.string.goods_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //执行删除方法
                        presenter.delete(str_uuid);
                    }
                });
                //设置取消按钮
                builder.setNegativeButton(R.string.popu_cancle, null);
                builder.create().show();
                break;
        }
    }

    @NonNull
    @Override
    public GoodsDetailPresenter createPresenter() {
        return new GoodsDetailPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        if (dialogFragment == null) {
            dialogFragment = new ProgressDialogFragment();
        }
        if (dialogFragment.isVisible()) return;
        dialogFragment.show(getSupportFragmentManager(), "Progress Dialog Fragment");
    }

    @Override
    public void hideProgress() {
        dialogFragment.dismiss();
    }

    @Override
    public void setImageData(ArrayList<String> arrayList) {
        list_uri = arrayList;
        //加载图片

        for (int i = 0; i < list_uri.size(); i++) {
            ImageView imageView = new ImageView(this);
            ImageLoader.getInstance().displayImage(EasyShopApi.IMAGE_URL + list_uri.get(i),
                    imageView, AvatarLoadOptions.build_item());
            list.add(imageView);
        }
        adapter.notifyDataSetChanged();
        indicator.setViewPager(viewPager);
    }

    @Override
    public void setData(GoodsDetail data, User goods_user) {
        this.goods_user = goods_user;
        tv_detail_name.setText(data.getName());
        tv_detail_price.setText(getString(R.string.goods_money, data.getPrice()));
        tv_detail_master.setText(getString(R.string.goods_detail_master, data.getMaster()));
        tv_detail_describe.setText(data.getDescription());
    }

    @Override
    public void showError() {
        tv_goods_error.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.goods_overdue);

    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void deleteEnd() {
        finish();
    }
}

