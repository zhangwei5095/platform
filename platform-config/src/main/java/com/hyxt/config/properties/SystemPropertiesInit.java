package com.hyxt.config.properties;

import com.hyxt.config.ConstantsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebEnvironment;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by rocky on 14-6-6.
 */
@Configuration
@Component(value = "systemPropertiesInit")
public class SystemPropertiesInit implements ApplicationContextAware , ConstantsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertiesInit.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("开始初始化内部properties文件");
        try {
            boolean isLoadedProjectProperties = loaderResources(applicationContext ,PROJECT_CUSTOM_PROPERTIES_LOCATION_RULE);
            if (!isLoadedProjectProperties) {
                LOGGER.error("加载框架内部配置文件，请在classpath下config文件中配置projectSystem.properties文件！");
                loaderResources(applicationContext , PROJECT_SYSTEM_PROPERTIES_LOCATION);
            }

        } catch (IOException e) {
            LOGGER.error("加载文件失败: {} , {}" , e.getMessage() , e);
        }
        LOGGER.info("结束初始化内部properties文件");
    }

    private boolean loaderResources(ApplicationContext applicationContext , String projectCustomFilePath) throws IOException {
        boolean isLoadedProjectProperties = false ;
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(projectCustomFilePath);
        Object standardServletEnvironment = applicationContext.getEnvironment();
        if (standardServletEnvironment instanceof ConfigurableWebEnvironment) {
            ConfigurableWebEnvironment configurableWebEnvironment = ((ConfigurableWebEnvironment)standardServletEnvironment);
            MutablePropertySources mutablePropertySources = configurableWebEnvironment.getPropertySources();
            for (Resource resource : resources) {
                LOGGER.info("配置文件路径: {}" , resource.getFile().getPath());
                if (configurableWebEnvironment.containsProperty(PROJECT_SYSTEM_PROPERTIES_NAME) &&
                        resource.getFile().getPath().endsWith(configurableWebEnvironment.getProperty(PROJECT_SYSTEM_PROPERTIES_NAME))){
                    isLoadedProjectProperties = true;
                }
                Properties properties = new Properties();
                properties.load(resource.getInputStream());
                PropertySource propertySource = new PropertiesPropertySource(resource.getFilename().split("\\.")[0], properties);
                mutablePropertySources.addLast(propertySource);
            }
        }
        return isLoadedProjectProperties;
    }
}
