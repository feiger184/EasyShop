package com.feicui.easyshop.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feicui.easyshop.R;
import com.feicui.easyshop.commons.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/2/7 0007.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.tv_register)
    TextView tvRegister;
    private ActivityUtils activityUtils;

    private String password;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        init();
    }

    private void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //给EditText添加监听
        etUsername.addTextChangedListener(textWatcher);
        etPwd.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //这里的charSequence表示改变之前的内容，通常i和i1组合，可以在s中读取本次改变字段中被改变的内容。
            //而after表示改变后新的内容的数量

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。
            //而before表示被改变的内容的数量。
        }

        @Override
        public void afterTextChanged(Editable editable) {
            username = etUsername.getText().toString();
            password = etPwd.getText().toString();

            //判断用户名和密码是否为空
            boolean canLogin = !(TextUtils.isEmpty(username) || TextUtils.isEmpty(password));
            btnLogin.setEnabled(canLogin);

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_login, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                activityUtils.showToast("执行登陆的网络请求");
                break;
            case R.id.tv_register:
                activityUtils.startActivity(RegisterActivity.class);
                break;
        }
    }
}
