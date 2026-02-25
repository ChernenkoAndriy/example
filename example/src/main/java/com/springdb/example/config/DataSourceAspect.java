package com.springdb.example.config;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.annotation.Order;

@Aspect
@Component
@Order(0)
public class DataSourceAspect {

    @Before("@annotation(transactional)")
    public void setDataSourceContext(Transactional transactional) {
        if (transactional.readOnly()) {
            DataSourceContextHolder.setBranchContext(DataSourceType.REPLICA);
        } else {
            DataSourceContextHolder.setBranchContext(DataSourceType.PRIMARY);
        }
    }

    @After("@annotation(transactional)")
    public void clear() {
        DataSourceContextHolder.clear();
    }
}