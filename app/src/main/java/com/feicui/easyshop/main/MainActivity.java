package com.feicui.easyshop.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;
import com.feicui.easyshop.main.me.MeFragment;
import com.feicui.easyshop.main.shop.ShopFragment;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindViews({R.id.tv_shop, R.id.tv_message, R.id.tv_mail_list, R.id.tv_me})
    TextView[] textviews;

    @BindView(R.id.main_toobar)
    Toolbar mainToobar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.main_title)
    TextView tv_title;

    private ActivityUtils activityUtils;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        init();

    }

    private void init() {
        viewpager.setAdapter(unLoginAdapter);

        textviews[0].setSelected(true);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (TextView textView : textviews) {
                    textView.setSelected(false);
                }
                //更改title，设置选择效果
                tv_title.setText(textviews[position].getText());
                textviews[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
    * 未登录时的ViewPager适配器
    * */
    private FragmentStatePagerAdapter unLoginAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ShopFragment();
                case 1:
                    return new UnLoginFragment();
                case 2:
                    return new UnLoginFragment();
                case 3:
                    return new MeFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    };


    @OnClick({R.id.tv_shop, R.id.tv_message, R.id.tv_mail_list, R.id.tv_me})
    public void onClick(TextView view) {
        for (int i = 0; i < textviews.length; i++) {
            textviews[i].setSelected(false);
            textviews[i].setTag(i);
        }

        //设置选择效果
        view.setSelected(true);
        //参数false代表瞬间切换，而不是平滑过渡
        viewpager.setCurrentItem((Integer) view.getTag(),false);
        //设置一下toolbar的title
        tv_title.setText(textviews[(Integer) view.getTag()].getText());
    }
    /*
    * 2秒内双击返回键 退出程序
    * */
    @Override
    public void onBackPressed() {

        if (!isExit) {
            isExit = true;
            activityUtils.showToast("再摁一次退出程序");
            viewpager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }


}

