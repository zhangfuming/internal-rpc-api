package com.internal.test.dummy.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by zhangfuming on 2014/6/30 18:16.
 */
@Configuration
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.internal","com.ytx.rpc.internal.api.spring"})
public class ApplicationConfig {

}
