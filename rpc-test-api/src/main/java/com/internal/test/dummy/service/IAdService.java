package com.internal.test.dummy.service;

import com.internal.test.dummy.beans.AdBean;
import com.ytx.rpc.internal.api.framework.annotation.RemoteService;

import java.util.Date;
import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 11:13.
 */
@RemoteService("adService")
public interface IAdService {

    public List<AdBean> getAdList(Date start ,Date end);

}
