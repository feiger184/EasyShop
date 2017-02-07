package com.feicui.easyshop.main;

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
 * 未登录时的Fragment
 */

public class UnLoginFragment extends Fragment {

    private View view;
    private ActivityUtils activityUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_un_login, container, false);
            ButterKnife.bind(this, view);
            activityUtils = new ActivityUtils(this);
        }
        return view;
    }
}
