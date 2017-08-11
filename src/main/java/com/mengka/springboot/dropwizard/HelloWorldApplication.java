package com.mengka.springboot.dropwizard;

import com.mengka.springboot.dropwizard.config.HelloWorldConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mengka
 * @date 2017/08/11.
 */
@Slf4j
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return Constant.APP_NAME;
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    }

    public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment) throws Exception {
        log.info("HelloWorldApplication run..");

        /**
         * 添加资源类
         * 从helloWorldConfiguration实例读取模板和默认名字，创建资源实例；
         */
        final HelloWorldResource resource = new HelloWorldResource(helloWorldConfiguration.getTemplate(), helloWorldConfiguration.getDefaultName());
        /**
         * 添加healthCheck
         */
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(helloWorldConfiguration.getTemplate());

        environment.healthChecks().register("template",healthCheck);
        environment.jersey().register(resource);
    }
}
