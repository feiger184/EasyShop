package com.feicui.easyshop;

import com.feicui.easyshop.commons.CurrentUser;
import com.feicui.easyshop.model.GetUsersResult;
import com.feicui.easyshop.model.User;
import com.feicui.easyshop.network.EasyShopClient;
import com.feicuiedu.apphx.model.repository.IRemoteUsersRepo;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 实现环信模块定义的远程用户仓库
 */

public class RemoteUserRepo implements IRemoteUsersRepo {
    @Override
    public List<EaseUser> queryByName(String username) throws Exception {
        Call call = EasyShopClient.getInstance().getSearchUser(username);
        Response response = call.execute();
        //如果失败
        if (!response.isSuccessful()) {
            throw new Exception(response.body().string());
        }
        String json = response.body().string();
        GetUsersResult result = new Gson().fromJson(json, GetUsersResult.class);

        //本地用户类转换成环信的用户类
        List<User> users = result.getDatas();
        List<EaseUser> easeUsers = CurrentUser.convertAll(users);

        return easeUsers;
    }

    @Override
    public List<EaseUser> getUsers(List<String> ids) throws Exception {

        Call call = EasyShopClient.getInstance().getUsers(ids);
        Response response = call.execute();
        //如果失败
        if (!response.isSuccessful()) {
            throw new Exception(response.body().string());
        }
        String json = response.body().string();
        GetUsersResult result = new Gson().fromJson(json, GetUsersResult.class);
        if (result.getCode() == 2) {
            throw new Exception(result.getMessage());
        }
        //本地用户类转换成环信的用户类
        List<User> users = result.getDatas();
        List<EaseUser> easeUsers = CurrentUser.convertAll(users);
        return easeUsers;
    }

}
