package com.hyxt.config.mybatis;

import com.hyxt.config.ConstantsConfig;
import com.hyxt.config.mybatis.page.PaginationInterception;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by rocky on 14-6-3.
 */
@Configuration
@Component
public class MybatisInit implements ApplicationContextAware , ConstantsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisInit.class);

    private ApplicationContext applicationContext ;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private DataSource getDataSource(){
        return applicationContext.getBean(DataSource.class);
    }

    @Bean
    @DependsOn(value = "systemPropertiesInit")
    public SqlSessionFactoryBean sqlSessionFactory() throws SQLException, IOException {
        LOGGER.info("init sqlSessionFactory");
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(getDataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage(applicationContext.getEnvironment().getProperty(PROJECT_MYBATIS_TYPE_ALIASES_PACKAGE));
        Properties properties = new Properties();
        properties.setProperty("sqlType" , applicationContext.getEnvironment().getProperty(PROJECT_MYBATIS_SQL_TYPE));
        sqlSessionFactoryBean.setConfigurationProperties(properties);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new PaginationInterception()});
        return sqlSessionFactoryBean;
    }

    @Bean
    @DependsOn(value = "systemPropertiesInit")
    public MapperScannerConfigurer mapperScannerConfigurer(){
        LOGGER.info("init mapperScannerConfigurer");
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(applicationContext.getEnvironment().getProperty(PROJECT_MYBATIS_BASE_PACKAGE));
        return mapperScannerConfigurer;
    }

}
