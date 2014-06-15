package com.hyxt.config.mybatis.page;

import com.hyxt.config.mybatis.page.dialect.Dialect;
import com.hyxt.config.mybatis.page.dialect.MysqlDialect;
import com.hyxt.config.mybatis.page.dialect.OracleDialect;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PaginationInterception implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {

        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        BoundSql boundSql = delegate.getBoundSql();
        Object temp = boundSql.getParameterObject();
        if (temp instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap) temp;
            Page page = (Page) paramMap.get("page");
            if (page != null) {
                Configuration configuration = (Configuration) ReflectUtil.getFieldValue(delegate, "configuration");
                if (page.getCount() == 0) {
                    MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
                    Connection connection = (Connection) invocation.getArgs()[0];
                    totalCount(connection, boundSql, configuration, mappedStatement, page);
                }
                ReflectUtil.setFieldValue(boundSql, "sql",
                        searchDialectByDbTypeEnum(configuration, page).spellPageSql(boundSql.getSql(), page.getOffset(), page.getLimit()));
            }
        }
        return invocation.proceed();
    }

    /**
     * 获取总页数
     *
     * @param conn
     * @param orgBoundSql
     * @param configuration
     * @param mappedStatement
     * @return
     * @throws java.sql.SQLException
     */
    private void totalCount(Connection conn, BoundSql orgBoundSql,
                            Configuration configuration, MappedStatement mappedStatement, Page page) throws SQLException {
        int totalCount = 0;
        String countSpellSql = searchDialectByDbTypeEnum(configuration, page).getCountSql(orgBoundSql.getSql());
        PreparedStatement preparedStatement = conn.prepareStatement(countSpellSql);
        Object parameterObject = orgBoundSql.getParameterObject();
        BoundSql boundSql = new BoundSql(configuration, countSpellSql,
                orgBoundSql.getParameterMappings(),
                parameterObject);
        setParameters(preparedStatement, mappedStatement, boundSql, parameterObject);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs != null) {
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
        }
        page.setCount(totalCount);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }


    /**
     * 获取方言对象
     *
     * @param page
     * @return
     */
    private Dialect searchDialectByDbTypeEnum(Configuration configuration, Page page) {
        Dialect dialect = null;
        switch (searchDbTypeByConfig(configuration)) {
            case MYSQL:
                dialect = new MysqlDialect(page.getOrderBy());
                break;
            case SQLSERVER:
            break;
            case DB2: //TODO
                break;
            default:
                dialect = new OracleDialect(page.getOrderBy());
                break;
        }
        return dialect;
    }

    /**
     * 获取数据库类型
     *
     * @return 返回数据库类型的枚举对象
     */
    private Dialect.Type searchDbTypeByConfig(Configuration configuration) {
        String dialectConfig = configuration.getVariables().getProperty("sqlType");
        if (StringUtils.isNotEmpty(dialectConfig)) {
            return Dialect.Type.valueOf(dialectConfig.toUpperCase());
        } else {
            throw new RuntimeException(
                    "databaseType is null , please check your mybatis configuration!");
        }
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }


    /**
     * 利用反射进行操作的一个工具类
     */
    private static class ReflectUtil {
        /**
         * 利用反射获取指定对象的指定属性
         *
         * @param obj       目标对象
         * @param fieldName 目标属性
         * @return 目标属性的值
         */
        public static Object getFieldValue(Object obj, String fieldName) {
            Object result = null;
            Field field = ReflectUtil.getField(obj, fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    result = field.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        /**
         * 利用反射获取指定对象里面的指定属性
         *
         * @param obj       目标对象
         * @param fieldName 目标属性
         * @return 目标字段
         */
        private static Field getField(Object obj, String fieldName) {
            Field field = null;
            for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    //这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
                }
            }
            return field;
        }

        /**
         * 利用反射设置指定对象的指定属性为指定的值
         *
         * @param obj        目标对象
         * @param fieldName  目标属性
         * @param fieldValue 目标值
         */
        public static void setFieldValue(Object obj, String fieldName,
                                         String fieldValue) {
            Field field = ReflectUtil.getField(obj, fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, fieldValue);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
