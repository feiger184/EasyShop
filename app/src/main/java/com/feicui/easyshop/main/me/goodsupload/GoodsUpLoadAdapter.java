package com.feicui.easyshop.main.me.goodsupload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.model.ImageItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 上传商品图片适配器
 */

public class GoodsUpLoadAdapter extends RecyclerView.Adapter {

    private ArrayList<ImageItem> list = new ArrayList<>();
    private LayoutInflater inflater;

    public GoodsUpLoadAdapter(Context context, ArrayList<ImageItem> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    // ######################    逻辑：模式的选择 start  ##########################
    //编辑时的模式，1=有图，2=无图（显示加号的布局）
    public static final int MODE_NORMAL = 1;
    public static final int MODE_MULTI_SELECT = 2;
    //代表图片编辑的模式
    public int mode;

    //用枚举，表示item类型，有图或无图
    public enum ITEM_TYPE {
        ITEM_NORMAL, ITEM_ADD
    }

    //模式设置
    public void changeMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    //获取当前模式
    public int getMode() {
        return mode;
    }
    // ######################    逻辑：模式的选择 over  ##########################

    public void add(ImageItem imageItem) {
        list.add(imageItem);
    }

    public int getSize() {
        return list.size();
    }

    public ArrayList<ImageItem> getList() {
        return list;
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    //确定ViewType的值
    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return ITEM_TYPE.ITEM_ADD.ordinal();
        }

        return ITEM_TYPE.ITEM_NORMAL.ordinal();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE.ITEM_NORMAL.ordinal()) {
            return new ItemSelectViewHolder(
                    inflater.inflate(R.layout.layout_item_recyclerview, parent, false));
        } else {
            return new ItemAddViewHolder(
                    inflater.inflate(R.layout.layout_item_recyclerviewlast, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //判断当前vh是不是ItemSelectViewHolder实例
        if (holder instanceof ItemSelectViewHolder) {
            //当前数据
            ImageItem imageItem = list.get(position);
            //拿到当前Vh(因为已经判断是vh的实例，所以强转)
            ItemSelectViewHolder item_select = (ItemSelectViewHolder) holder;
            //拿到当前数据
            item_select.photo = imageItem;
            //判断模式（正常，可删除）

            //判断模式
            if (mode == MODE_MULTI_SELECT) {
                //可选框可见
                item_select.checkBox.setVisibility(View.VISIBLE);
                //可选框设置监听
                item_select.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //imageItem中选择状态改变
                        list.get(position).setIsCheck(isChecked);
                    }
                });
                //勾选框改变(根据imageitem的选择状态)
                item_select.checkBox.setChecked(imageItem.isCheck());
            } else if (mode == MODE_NORMAL) {
                //隐藏可选框
                item_select.checkBox.setVisibility(View.GONE);
            }

            //图片设置
            item_select.ivPhoto.setImageBitmap(imageItem.getBitmap());
            //图片点击事件监听
            item_select.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: 2017/2/17 0017 tupian地方
                }
            });

            item_select.ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //模式改为删除模式
                    mode = MODE_MULTI_SELECT;
                    //更新
                    notifyDataSetChanged();
                    // TODO: 2017/2/17 0017 执行长按的监听事件
                    return false;
                }

            });

        } else if (holder instanceof ItemAddViewHolder) {

            ItemAddViewHolder item_add = (ItemAddViewHolder) holder;
            if (position == 8) {
                item_add.ib_add.setVisibility(View.GONE);

            } else {
                item_add.ib_add.setVisibility(View.VISIBLE);
            }
            item_add.ib_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/2/17 0017 点击图片
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return Math.min(list.size() + 1, 8);
    }

    //显示添加按钮的Viewholder
    public static class ItemAddViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ib_recycle_add)
        ImageButton ib_add;

        public ItemAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    //已经有图片的ViewHolder
    public static class ItemSelectViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.cb_check_photo)
        CheckBox checkBox;

        ImageItem photo;

        public ItemSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
