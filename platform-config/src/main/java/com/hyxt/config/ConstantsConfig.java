package com.hyxt.config;

/**
 * Created by rocky on 14-6-6.
 */
public interface ConstantsConfig {

    public static final String CLASSPATH_PREFIX = "classpath*:/";

    /**
     * 项目配置文件名称
     */
    public static final String PROJECT_SYSTEM_PROPERTIES_NAME = "projectSystem.properties";

    public static final String PROJECT_SYSTEM_PROPERTIES_LOCATION = CLASSPATH_PREFIX + PROJECT_SYSTEM_PROPERTIES_NAME;

    public static final String PROJECT_CUSTOM_PROPERTIES_LOCATION_RULE = CLASSPATH_PREFIX + "config/*.properties";

    public static final String PROJECT_MYBATIS_TYPE_ALIASES_PACKAGE = "mybatis.config.typeAliasesPackage";

    public static final String PROJECT_MYBATIS_SQL_TYPE = "mybatis.config.sqlType";

    public static final String PROJECT_MYBATIS_BASE_PACKAGE = "mybatis.config.basePackage";

    public static final String PROJECT_DRUID_DATASOURCE_USERNAME = "druid.datasource.username";

    public static final String PROJECT_DRUID_DATASOURCE_URL = "druid.datasource.url";

    public static final String PROJECT_DRUID_DATASOURCE_PASSWORD = "druid.datasource.password";

    public static final String PROJECT_DRUID_DATASOURCE_FILTERS = "druid.datasource.filters";

    public static final String PROJECT_DRUID_DATASOURCE_MAXACTIVE = "druid.datasource.maxActive";

    public static final String PROJECT_DRUID_DATASOURCE_INITIALSIZE = "druid.datasource.initialSize";

    public static final String PROJECT_DRUID_DATASOURCE_MAXWAIT = "druid.datasource.maxWait";

    public static final String PROJECT_DRUID_DATASOURCE_MINIDLE = "druid.datasource.minIdle";

    public static final String PROJECT_DRUID_DATASOURCE_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "druid.datasource.timeBetweenEvictionRunsMillis";

    public static final String PROJECT_DRUID_DATASOURCE_MIN_EVICTABLE_IDLE_TIME_MILLIS = "druid.datasource.minEvictableIdleTimeMillis";

    public static final String PROJECT_DRUID_DATASOURCE_VALIDATION_QUERY = "druid.datasource.validationQuery";

    public static final String PROJECT_DRUID_DATASOURCE_TEST_WHILE_IDLE = "druid.datasource.testWhileIdle";

    public static final String PROJECT_DRUID_DATASOURCE_TEST_ON_BORROW = "druid.datasource.testOnBorrow";

    public static final String PROJECT_DRUID_DATASOURCE_TEST_ON_RETURN = "druid.datasource.testOnReturn";

    public static final String PROJECT_DRUID_DATASOURCE_POOL_PREPARED_STATEMENTS = "druid.datasource.poolPreparedStatements";

    public static final String PROJECT_DRUID_DATASOURCE_MAX_OPEN_PREPARED_STATEMENTS = "druid.datasource.maxOpenPreparedStatements";

    public static final String PROJECT_DRUID_DATASOURCE_SLOW_SQL_MILLIS = "druid.datasource.slowSqlMillis";

    public static final String PROJECT_DRUID_DATASOURCE_IS_LOG_SLOW_SQL = "druid.datasource.isLogSlowSql";

}
