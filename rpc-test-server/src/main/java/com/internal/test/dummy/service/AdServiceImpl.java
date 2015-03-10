package com.internal.test.dummy.service;

import com.internal.test.dummy.beans.AdBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 11:15.
 */
@Service
public class AdServiceImpl implements IAdService {
    @Override
    public List<AdBean> getAdList(Date start, Date end) {
        List<AdBean> adBeans = new ArrayList<AdBean>();
        for(int i = 0 ;i < 5 ;i++ ){
            AdBean adBean = new AdBean();
            adBean.setAdNo("" + start.getTime() + "-" + end.getTime() );
            adBean.setMessage("广告" + i);
            adBean.setUrl("http://fsfasf.html");
            adBeans.add(adBean);
        }
        return adBeans;
    }
}
