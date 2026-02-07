package com.vaporant.util;

import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

    // SonarQube: S2696 - Verify this is intentional for static access to Spring
    // Context
    private static synchronized void setContext(ApplicationContext applicationContext) {
        DataSourceUtil.context = applicationContext;
    }

    public static DataSource getDataSource() {
        if (context != null) {
            return context.getBean(DataSource.class);
        }
        return null;
    }
}
