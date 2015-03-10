package com.internal.test.dummy.service;

import com.internal.test.dummy.beans.UserBean;
import com.ytx.rpc.internal.api.framework.annotation.RemoteService;

import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 10:55.
 */
@RemoteService("userService")
public interface IUserService {

    public List<UserBean> getUserList(String server);

    public boolean addUser(UserBean userBean);

}
