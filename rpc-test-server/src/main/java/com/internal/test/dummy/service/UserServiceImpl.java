package com.internal.test.dummy.service;

import com.internal.test.dummy.beans.UserBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 10:56.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Override
    public List<UserBean> getUserList(String server) {
        if(StringUtils.isBlank(server)){
            return Collections.<UserBean> emptyList();
        }
        List<UserBean> list = new ArrayList<UserBean>();
        for(int i = 0 ;i< 10;i++){
            UserBean userBean = new UserBean();
            userBean.setId(1L * i);
            userBean.setUserName(String.format("测试用户-%s-%d",server,i));
            userBean.setEmail("xxxxxx@baidao.com");
            userBean.setImgUrl("http://2222222.jpg");
            list.add(userBean);
        }
        return list;
    }

    @Override
    public boolean addUser(UserBean userBean) {
        System.out.println(ToStringBuilder.reflectionToString(userBean));
        return true;
    }
}
