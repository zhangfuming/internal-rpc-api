package com.internal.test.client;

import com.internal.test.dummy.beans.UserBean;
import com.internal.test.dummy.service.IUserService;
import com.ytx.rpc.internal.api.client.factory.RemoteServiceFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangfuming on 2015/2/9 11:26.
 */
public class ClientTest {

    private ExecutorService executorService = Executors.newFixedThreadPool(30,new TestThreadFactory());

    @Test
    public void testAddUser() throws InterruptedException {
        final IUserService userService = RemoteServiceFactory.createRemoteService(IUserService.class);
        try{
            for(int i = 0 ;i< 100000;i++){
                executorService.submit( new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserBean userBean = new UserBean();
                        userBean.setId(1L);
                        userBean.setUserName("张三");
                        userBean.setEmail("9047234327@baidao.com");
                        userBean.setImgUrl("http://313123.jpg");
                        System.out.println(userService.addUser(userBean));
                    }
                }));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Thread.sleep(2 * 60 * 1000);
    }

    @Test
    public void testGetUserList(){
        IUserService userService = RemoteServiceFactory.createRemoteService(IUserService.class);
        List<UserBean> list = userService.getUserList("TT");
        for(UserBean userBean : list){
            System.out.println(ToStringBuilder.reflectionToString(userBean));
        }
    }


    static class TestThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TestThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "test-pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }



}
