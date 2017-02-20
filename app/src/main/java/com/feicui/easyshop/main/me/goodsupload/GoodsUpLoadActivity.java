package com.feicui.easyshop.main.me.goodsupload;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.commons.ImageUtils;
import com.feicui.easyshop.commons.MyFileUtils;
import com.feicui.easyshop.components.PicWindow;
import com.feicui.easyshop.components.ProgressDialogFragment;
import com.feicui.easyshop.model.CachePreferences;
import com.feicui.easyshop.model.GoodsUpLoad;
import com.feicui.easyshop.model.ImageItem;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsUpLoadActivity extends MvpActivity<GoodsUpLoadView, GoodsUpLoadPresenter> implements GoodsUpLoadView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_goods_name)
    EditText et_goods_name;
    @BindView(R.id.et_goods_price)
    EditText et_goods_price;
    @BindView(R.id.et_goods_describe)
    EditText et_goods_describe;
    @BindView(R.id.tv_goods_type)
    TextView tv_goods_type;
    @BindView(R.id.tv_goods_delete)
    TextView tv_goods_delete;
    @BindView(R.id.btn_goods_load)
    Button btn_goods_load;


    private final String[] goods_type = {"家用", "电子", "服饰", "玩具", "图书", "礼品", "其它"};
    /*商品种类为自定义*/
    private final String[] goods_type_num = {"household", "electron", "dress", "toy", "book", "gift", "other"};


    private ActivityUtils activityUtils;
    private String str_goods_name;//商品名
    private String str_goods_price;//商品价格
    private String str_goods_type = goods_type_num[0];//商品类型（默认家用）
    private String str_goods_describe;//商品描述

    //模式：普通=1
    public static final int MODE_DONE = 1;
    //模式：删除=2
    public static final int MODE_DELETE = 2;
    private int title_mode = MODE_DONE;
    private ArrayList<ImageItem> list = new ArrayList<>();
    private GoodsUpLoadAdapter adatper;
    private PicWindow picWindow;
    private ProgressDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_up_load);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();//初始化视图
    }

    //toolbar返回要实现的方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public GoodsUpLoadPresenter createPresenter() {
        return new GoodsUpLoadPresenter();
    }

    private void initView() {
        picWindow = new PicWindow(this, listener);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        //设置默认动画（item增删动画）
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置固定大小
        recyclerView.setHasFixedSize(true);

        //获取缓存文件夹中的文件
        list = getFilePhoto();

        adatper = new GoodsUpLoadAdapter(this, list);
        adatper.setListener(itemClickedListener);
        recyclerView.setAdapter(adatper);

        //商品名称，价格，描述输入的监听
        et_goods_name.addTextChangedListener(textWatcher);
        et_goods_price.addTextChangedListener(textWatcher);
        et_goods_describe.addTextChangedListener(textWatcher);


    }

    //图片选择弹窗内的监听事件
    private PicWindow.Listener listener = new PicWindow.Listener() {
        @Override
        public void toGallery() {
            //从相册
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            //从相机
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };

    private CropHandler cropHandler = new CropHandler() {
        @Override
        public void onPhotoCropped(Uri uri) {
            //需求：裁剪完成后，把图片保存为bitmap，并且保存到SD中，并且展示出来
            //文件名：就是用系统当前时间，不重名
            String fileName = String.valueOf(System.currentTimeMillis());
            Bitmap bitmap = ImageUtils.readDownsampledImage(uri.getPath(), 1080, 1920);
            //保存到SD卡中
            MyFileUtils.saveBitmap(bitmap, fileName);
            ImageItem photo = new ImageItem();
            photo.setBitmap(bitmap);
            photo.setImagePath(fileName + ".JPEG");
            adatper.add(photo);
            adatper.notifyData();
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
            return GoodsUpLoadActivity.this;
        }

    };

    //当Acticity拿到图片裁剪的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
    }

    public ArrayList<ImageItem> getFilePhoto() {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        //拿到所有图片文件
        File[] files = new File(MyFileUtils.SD_PATH).listFiles();
        if (files != null) {
            for (File file : files) {
                Bitmap bitmap = BitmapFactory.decodeFile(MyFileUtils.SD_PATH + file.getName());
                ImageItem item = new ImageItem();
                item.setBitmap(bitmap);
                item.setImagePath(file.getName());
                imageItems.add(item);

            }
        }
        return imageItems;
    }

    //适配器中自定义的监听事件
    private GoodsUpLoadAdapter.OnItemClickListener itemClickedListener = new GoodsUpLoadAdapter.OnItemClickListener() {
        @Override
        public void onAddClicked() {
            //单击，添加图片
            if (picWindow != null && picWindow.isShowing()) {
                picWindow.dismiss();
            } else if (picWindow != null) {
                picWindow.show();
            }

        }

        @Override
        public void onPhotoClicked(ImageItem photo, ImageView imageView) {
            //单机，跳转到图片展示页
            // 跳转到图片展示页
            Intent intent = new Intent(GoodsUpLoadActivity.this, GoodsUpLoadImageShowActivity.class);
            intent.putExtra("images", photo.getBitmap());
            intent.putExtra("width", imageView.getWidth());
            intent.putExtra("height", imageView.getHeight());
            startActivity(intent);

        }

        @Override
        public void onLongClicked() {
            //长按，执行删除相关
            //模式改为可删除模式
            title_mode = MODE_DELETE;
            //删除的tv可见
            tv_goods_delete.setVisibility(View.VISIBLE);
        }
    };


    //商品名称，价格，描述输入的监听
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            str_goods_name = et_goods_name.getText().toString();
            str_goods_price = et_goods_price.getText().toString();
            str_goods_describe = et_goods_describe.getText().toString();
            //判断上传按钮是否可点击
            boolean can_save = !(TextUtils.isEmpty(str_goods_name) || TextUtils.isEmpty(str_goods_price)
                    || TextUtils.isEmpty(str_goods_describe));
            btn_goods_load.setEnabled(can_save);
        }
    };

    //重写返回方法，实现点击改变模式
    @Override
    public void onBackPressed() {
        if (title_mode == MODE_DONE) {
            //删除缓存
            deleteCache();
            finish();
        } else if (title_mode == MODE_DELETE) {
            //转变模式
            changeModeActivity();
        }
    }

    //删除缓存（删除缓存文件夹中的文件）
    private void deleteCache() {
        for (int i = 0; i < adatper.getList().size(); i++) {
            MyFileUtils.delFile(adatper.getList().get(i).getImagePath());
        }
    }

    //转变模式
    private void changeModeActivity() {
        //判断，根据adapter判断当前模式是否是可删除模式
        if (adatper.getMode() == GoodsUpLoadAdapter.MODE_MULTI_SELECT) {
            //删除tv不可见
            tv_goods_delete.setVisibility(View.GONE);
            //activity模式改变
            title_mode = MODE_DONE;
            //adapter模式改变
            adatper.changeMode(GoodsUpLoadAdapter.MODE_NORMAL);
            for (int i = 0; i < adatper.getList().size(); i++) {
                adatper.getList().get(i).setIsCheck(false);
            }
        }
    }

    //点击删除，商品类型，上传的监听
    @OnClick({R.id.tv_goods_delete, R.id.btn_goods_type, R.id.btn_goods_load})
    public void onClick(View view) {
        switch (view.getId()) {
            //商品类型
            case R.id.btn_goods_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("商品类型");
                builder.setItems(goods_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //显示商品类型
                        tv_goods_type.setText(goods_type[which]);
                        //拿到商品类型的英文描述（用于上传）
                        str_goods_type = goods_type_num[which];
                    }
                });
                builder.create().show();
                break;
            //点击删除
            case R.id.tv_goods_delete:
                ArrayList<ImageItem> del_list = adatper.getList();
                int num = del_list.size();
                for (int i = num - 1; i >= 0; i--) {
                    //删除规则（checkbox被选中）
                    if (del_list.get(i).isCheck()) {
                        //删除缓存文件夹中的文件
                        MyFileUtils.delFile(del_list.get(i).getImagePath());
                        del_list.remove(i);
                    }
                }
                this.list = del_list;
                adatper.notifyData();
                changeModeActivity();
                title_mode = MODE_DONE;
                break;
            //点击上传
            case R.id.btn_goods_load:
                if (adatper.getSize() == 0) {
                    activityUtils.showToast("最少有一张商品图片");
                    return;
                }
                presenter.upLoad(setGoodsInfo(), list);
                break;
        }
    }

    //对商品信息做初始化
    private GoodsUpLoad setGoodsInfo() {
        GoodsUpLoad goodsLoad = new GoodsUpLoad();
        goodsLoad.setName(str_goods_name);
        goodsLoad.setPrice(str_goods_price);
        goodsLoad.setDescribe(str_goods_describe);
        goodsLoad.setType(str_goods_type);
        goodsLoad.setMaster(CachePreferences.getUser().getName());
        return goodsLoad;
    }


    @Override
    public void showPrb() {
        if (dialogFragment == null) dialogFragment = new ProgressDialogFragment();
        if (dialogFragment.isVisible()) return;
        dialogFragment.show(getSupportFragmentManager(), "Progress Dialog Fragment");
    }

    @Override
    public void hidePrb() {
        dialogFragment.dismiss();

    }

    @Override
    public void upLoadSuccess() {
        finish();

    }

    @Override
    public void showMsg(String msg) {
        activityUtils.showToast(msg);

    }

}
