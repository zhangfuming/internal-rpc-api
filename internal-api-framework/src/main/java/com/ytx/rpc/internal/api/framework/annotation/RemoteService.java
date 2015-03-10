package com.ytx.rpc.internal.api.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by zhangfuming on 2015/2/9 10:06.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RemoteService {
    String value();
}
