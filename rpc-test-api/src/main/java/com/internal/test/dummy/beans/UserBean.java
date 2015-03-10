package com.internal.test.dummy.beans;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/2/9 11:03.
 */
public class UserBean implements Serializable {
    private static final long serialVersionUID = -8423224641258774155L;

    private Long id;

    private String userName;

    private String imgUrl;

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
