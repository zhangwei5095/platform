package com.hyxt.config.datasource;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.hyxt.config.ConstantsConfig;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rocky on 14-6-1.
 */
@Configuration
@Component
public class PlatformDataSource implements ApplicationContextAware , ConstantsConfig {

    private ApplicationContext applicationContext;

    @Bean(destroyMethod = "close" , initMethod = "init")
    @DependsOn(value = "systemPropertiesInit")
    public DataSource dataSource() throws SQLException {
        Environment env = getEnv();
        DruidDataSource druidDataSource = new DruidDataSource(false);
        druidDataSource.setUsername(env.getProperty(PROJECT_DRUID_DATASOURCE_USERNAME));
        druidDataSource.setUrl(env.getProperty(PROJECT_DRUID_DATASOURCE_URL));
        druidDataSource.setPassword(env.getProperty(PROJECT_DRUID_DATASOURCE_PASSWORD));
        druidDataSource.setFilters(env.getProperty(PROJECT_DRUID_DATASOURCE_FILTERS));
        druidDataSource.setMaxActive(Integer.valueOf(env.getProperty(PROJECT_DRUID_DATASOURCE_MAXACTIVE)));
        druidDataSource.setInitialSize(Integer.valueOf(env.getProperty(PROJECT_DRUID_DATASOURCE_INITIALSIZE)));
        druidDataSource.setMaxWait(Integer.valueOf(env.getProperty(PROJECT_DRUID_DATASOURCE_MAXWAIT)));
        druidDataSource.setMinIdle(Integer.valueOf(env.getProperty(PROJECT_DRUID_DATASOURCE_MINIDLE)));
        druidDataSource.setTimeBetweenEvictionRunsMillis(env.getProperty(PROJECT_DRUID_DATASOURCE_TIME_BETWEEN_EVICTION_RUNS_MILLIS , Integer.class));
        druidDataSource.setMinEvictableIdleTimeMillis(env.getProperty(PROJECT_DRUID_DATASOURCE_MIN_EVICTABLE_IDLE_TIME_MILLIS , Integer.class));
        druidDataSource.setValidationQuery(env.getProperty(PROJECT_DRUID_DATASOURCE_VALIDATION_QUERY));
        druidDataSource.setTestWhileIdle(env.getProperty(PROJECT_DRUID_DATASOURCE_TEST_WHILE_IDLE , Boolean.class));
        druidDataSource.setTestOnBorrow(env.getProperty(PROJECT_DRUID_DATASOURCE_TEST_ON_BORROW , Boolean.class));
        druidDataSource.setTestOnReturn(env.getProperty(PROJECT_DRUID_DATASOURCE_TEST_ON_RETURN , Boolean.class));
        druidDataSource.setPoolPreparedStatements(env.getProperty(PROJECT_DRUID_DATASOURCE_POOL_PREPARED_STATEMENTS , Boolean.class));
        druidDataSource.setMaxOpenPreparedStatements(env.getRequiredProperty(PROJECT_DRUID_DATASOURCE_MAX_OPEN_PREPARED_STATEMENTS , Integer.class));
        return druidDataSource;
    }

    @Bean( name = "stat-filter")
    public StatFilter statFilter() {
        Environment env = getEnv();
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(env.getProperty(PROJECT_DRUID_DATASOURCE_SLOW_SQL_MILLIS , Integer.class));
        statFilter.setLogSlowSql(env.getProperty(PROJECT_DRUID_DATASOURCE_IS_LOG_SLOW_SQL, Boolean.class));
        return statFilter;
    }

    @Bean(name = "druidWebStatFilter")
    public FilterRegistrationBean druidWebStatFilter(){
        FilterRegistrationBean filterRegistrationBean =  new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParameters = new HashMap<String, String>(1);
        initParameters.put("exclusions" , "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean druid() throws SQLException {
        return new ServletRegistrationBean(new StatViewServlet() , "/platform/*");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Environment getEnv(){
        return applicationContext.getEnvironment();
    }
}
