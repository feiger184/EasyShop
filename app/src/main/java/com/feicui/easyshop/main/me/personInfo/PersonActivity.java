package com.feicui.easyshop.main.me.personInfo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.components.AvatarLoadOptions;
import com.feicui.easyshop.components.PicWindow;
import com.feicui.easyshop.components.ProgressDialogFragment;
import com.feicui.easyshop.main.MainActivity;
import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.ItemShow;
import com.feicui.easyshop.model.User;
import com.feicui.easyshop.network.EasyShopApi;
import com.feicuiedu.apphx.model.HxUserManager;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hyphenate.easeui.controller.EaseUI;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonActivity extends MvpActivity<PersonView, PersonPersenter> implements PersonView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_user_head)
    ImageView ivUserHead;//用户头像
    @BindView(R.id.listView)
    ListView listView;//显示用户名，昵称，环信ID的listView

    private ActivityUtils activityUtils;
    private PersonAdapter adapter;
    private List<ItemShow> list = new ArrayList<>();
    private ProgressDialogFragment dialogFragment;
    private PicWindow picWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new PersonAdapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);

        //获取用户头像
        updataAvatar(CachePreferences.getUser().getHead_Image());
    }

    @NonNull
    @Override
    public PersonPersenter createPresenter() {
        return new PersonPersenter();
    }


    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        init();
        adapter.notifyDataSetChanged();
    }


    /*
    *  数据初始化
    * */
    private void init() {

        User user = CachePreferences.getUser();
        list.add(new ItemShow(getResources().getString(R.string.username), user.getName()));
        list.add(new ItemShow(getResources().getString(R.string.nickname), user.getNick_name()));
        list.add(new ItemShow(getResources().getString(R.string.hx_id), user.getHx_Id()));
    }

    @OnClick({R.id.btn_login_out, R.id.iv_user_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user_head:
                if (picWindow == null) {
                    picWindow = new PicWindow(this, listener);
                }
                if (picWindow.isShowing()) {
                    picWindow.dismiss();
                    return;
                }
                picWindow.show();
                break;
            case R.id.btn_login_out:

                //清空本地配置
                CachePreferences.clearAllData();
                //清除所有旧的Activity
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // 退出环信相关
                HxUserManager.getInstance().asyncLogout();
                //登出关掉通知栏中的通知
                EaseUI.getInstance().getNotifier().reset();

        }
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            switch (position) {
                case 0:
                    activityUtils.showToast(getResources().getString(R.string.username_update));
                    break;
                case 1:
                    activityUtils.startActivity(NickNameActivity.class);
                    break;
                case 2:
                    activityUtils.showToast(getResources().getString(R.string.id_update));
                    break;

            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }


    private PicWindow.Listener listener = new PicWindow.Listener() {
        @Override
        public void toGallery() {
            //从相册里取
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);

        }
    };


    private CropHandler cropHandler = new CropHandler() {
        @Override
        public void onPhotoCropped(Uri uri) {
            //通过uri拿到图片文件
            File file = new File(uri.getPath());
            // 上传头像
            presenter.updataAvatar(file);
        }

        @Override
        public void onCropCancel() {

        }

        @Override
        public void onCropFailed(String message) {

        }

        @Override
        public CropParams getCropParams() {
            CropParams cropParams = new CropParams();
            cropParams.aspectX = 400;
            cropParams.aspectY = 400;

            return cropParams;
        }

        @Override
        public Activity getContext() {
            return PersonActivity.this;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
    }

    @Override
    public void showPrb() {
        if (dialogFragment == null) {
            dialogFragment = new ProgressDialogFragment();
        }
        if (dialogFragment.isVisible()) return;
        dialogFragment.show(getSupportFragmentManager(), "progress dialog fragment");
    }

    @Override
    public void hidePrb() {
        dialogFragment.dismiss();
    }

    @Override
    public void showMsg(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void updataAvatar(String url) {
        //头像加载
        ImageLoader.getInstance()
                .displayImage(EasyShopApi.IMAGE_URL + url,
                        ivUserHead, AvatarLoadOptions.build());
    }
}
