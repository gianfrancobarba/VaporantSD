package com.vaporant.util;

import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceUtil implements ApplicationContextAware {

    private static DataSourceUtil instance;
    private ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        instance = this;
    }

    public static DataSource getDataSource() {
        if (instance != null && instance.context != null) {
            return instance.context.getBean(DataSource.class);
        }
        return null;
    }
}
