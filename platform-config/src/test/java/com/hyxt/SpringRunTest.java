package com.hyxt;

import com.hyxt.config.properties.SystemConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by rocky on 14-6-5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationRun.class)
@WebAppConfiguration
public class SpringRunTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRunTest.class);

    @Autowired
    SystemConfig systemConfig;

    @Test
    public void testConfig(){
        LOGGER.debug("this is testing multiple properties ");
        LOGGER.debug(systemConfig.getValue("test", String.class));
        LOGGER.debug(systemConfig.getValue("aaa", String.class));
        LOGGER.debug("this is testing multiple properties ");
    }

}
