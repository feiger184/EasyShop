package com.feicui.easyshop.main.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;

import butterknife.ButterKnife;

/**
 * 我的页面
 */

public class MeFragment extends Fragment {

    private View view;
    private ActivityUtils activityUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_me, container, false);
            ButterKnife.bind(this, view);
            activityUtils = new ActivityUtils(this);
        }
        return view;
    }

}
