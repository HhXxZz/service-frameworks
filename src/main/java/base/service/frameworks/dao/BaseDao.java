package base.service.frameworks.dao;

import base.service.frameworks.utils.BeanUtil;
import base.service.frameworks.utils.ConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import base.service.frameworks.dao.builder.SQLBuilder;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * <pre>
 * Created by someone on 2018-08-07.
 *
 * </pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BaseDao {
    // ===========================================================
    // Constants
    // ===========================================================
    //private Logger SQL_LOG = Logger.getLogger(BaseDao.class.getName().concat("#SQL"));
    private static final Logger LOG = LogManager.getLogger(BaseDao.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected JdbcTemplate mTemplate;

    // ===========================================================
    // Constructors
    // ===========================================================
    public BaseDao(){
        this.mTemplate = ConnectionUtil.INSTANCE.getJDBC();
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 插入对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @return 主键
     */
    public long insert(Object pEntity){
        return insert(pEntity, true);
    }

    /**
     * 插入对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @param pAutoIncrement 是否自增，如果false会更新主键
     * @return 主键
     */
    public long insert(Object pEntity, boolean pAutoIncrement){
        return insert(pEntity, pAutoIncrement, null, null, null);
    }

    /**
     * 插入对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @param pAutoIncrement 是否自增，如果false会更新主键
     * @param pDuplicateUpdates 冲突更新字段
     * @return 主键
     */
    public long insert(Object pEntity, boolean pAutoIncrement, String[] pDuplicateUpdates){
        return insert(pEntity, pAutoIncrement, pDuplicateUpdates, null, null);
    }

    /**
     * 插入对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @param pAutoIncrement 是否自增，如果false会更新主键
     * @param pDuplicateUpdates 冲突更新字段
     * @param pIncludes 限定插入字段
     * @param pExcludes 排除插入字段
     * @return 主键
     */
    public long insert(Object pEntity, boolean pAutoIncrement, String[] pDuplicateUpdates, String[] pIncludes, String[] pExcludes){
        if(pEntity != null) {
            ready();

            String    insert  = new SQLBuilder(pEntity).onDuplicateKeyUpdate(pDuplicateUpdates).includeFields(pIncludes).excludeFields(pExcludes).insert(pAutoIncrement);
            KeyHolder primary = new GeneratedKeyHolder();
            // 执行插入语句
            mTemplate.update(connection -> connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS), primary);
            Number generatedKey = primary.getKey();
            return generatedKey == null ? 0 : generatedKey.longValue();
        }
        return 0;
    }

    /**
     * 插入对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pSQL     PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 主键
     */
    public long insert(final String pSQL, final Object[] pParams){
        ready();

        KeyHolder primary = new GeneratedKeyHolder();
        // 执行插入语句
        int effect = mTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(pSQL, Statement.RETURN_GENERATED_KEYS);
            if (pParams != null) {
                int i = 1;
                for (Object param : pParams) {
                    ps.setObject(i++, param);
                }
            }
            return ps;
        }, primary);
        switch (effect){
            case 1:
                Number generatedKey = primary.getKey();
                return generatedKey == null ? 0 : generatedKey.longValue();
            case 2:
                List<Map<String, Object>> keyList = primary.getKeyList();
                if(keyList.size() > 0){
                    Optional<Object> optional = keyList.get(0).values().stream().findFirst();
                    if(optional.isPresent()) {
                        Object key = optional.get();
                        if (key instanceof Number) {
                            return ((Number) key).longValue();
                        }
                    }
                }
                return 0;
            case 0:
            default:
                return 0;
        }
    }

    /**
     * 更新对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @return 是否成功
     */
    public boolean update(Object pEntity){
        return update(pEntity, null, null, null);
    }

    /**
     * 更新对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @param pConditionFields 条件字段，无条件时使用主键为条件；有条件时，条件为字段等于对象中的值；
     * @return 是否成功
     */
    public boolean update(Object pEntity, String[] pConditionFields){
        return update(pEntity, pConditionFields, null, null);
    }

    /**
     * 更新对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 更新内容
     * @param pConditionFields 条件字段，无条件时使用主键为条件；有条件时，条件为字段等于对象中的值；
     * @param pIncludes 限定更新字段
     * @param pExcludes 排除更新字段
     * @return 是否成功
     */
    public boolean update(Object pEntity, String[] pConditionFields, String[] pIncludes, String[] pExcludes){
        if(pEntity != null) {
            ready();

            String update = new SQLBuilder(pEntity).conditions(pConditionFields).includeFields(pIncludes).excludeFields(pExcludes).update();
            return mTemplate.update(update) != 0;
        }
        return false;
    }

    /**
     * 删除对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 删除内容
     * @return 是否成功
     */
    public boolean delete(Object pEntity){
        return delete(pEntity, null);
    }

    /**
     * 删除对象
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pEntity 删除内容
     * @param pConditionFields 条件字段，无条件时使用主键为条件；有条件时，条件为字段等于对象中的值；
     * @return 是否成功
     */
    public boolean delete(Object pEntity, String[] pConditionFields){
        if(pEntity != null) {
            ready();

            String delete = new SQLBuilder(pEntity).conditions(pConditionFields).delete();
            return mTemplate.update(delete) != 0;
        }
        return false;
    }

    /**
     * 获取第一个对象，此方法通过类构造 select 语句
     * 1. 对象需要 @Table 定义表名
     * 2. 对象需要 @PrimaryKey 定义主键，否则默认主键为 seqid
     * @param pClass 查询的类
     * @param pConditionFields 条件字段，无条件时使用主键为条件；有条件时，条件为字段等于对象中的值；
     * @param pObject 查询的类Value
     * @return 查询满足条件的第一个对象
     */
    public <T> T first(Class<T> pClass, String[] pConditionFields, Object pObject) {
        if(pClass != null) {
            ready();

            String select;
            try {
                select = new SQLBuilder(pObject).conditions(pConditionFields).find();
                return first(pClass, select, null);
            } catch (Exception e) {
                LOG.error("first query with SQLBuilder failed.", e);
            }
        }
        return null;
    }

    /**
     * 获取第一个对象
     *
     * @param pClass  列表元素类型，一般为pojo类
     * @param pSQL    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 对象
     */
    public <T> T first(final Class<T> pClass, final String pSQL, final Object[] pParams) {
        List<T> result = query(pClass, pSQL, pParams);
        T       obj    = null;
        if (result.size() > 0) {
            obj = result.get(0);
        }
        return obj;
    }

    /**
     * 获取列表
     *
     * @param pClass  列表元素类型，一般为pojo类
     * @param pSQL    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 结果集
     */
    public <T> List<T> query(final Class<T> pClass, final String pSQL, final Object[] pParams) {
        ready();

        return mTemplate.execute(connection -> {
            PreparedStatement ps = connection.prepareStatement(pSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (pParams != null) {
                int i = 1;
                for (Object param : pParams) {
                    ps.setObject(i++, param);
                }
            }
            return ps;
        }, new PreparedStatementCallback<List<T>>() {
            ResultSetMetaData metaData;

            public List<T> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                List<T>   result = new ArrayList<>();
                ResultSet rs     = ps.executeQuery();
                metaData = rs.getMetaData();
                int colTotal = metaData.getColumnCount();
                // 判断是否有设置值到obj中
                try {
                    while (rs.next()) {
                        int hasValue = 0;
                        T   obj      = pClass.newInstance();
                        // 填充所有可能的属性
                        Field[] fieldArr = getFields();
                        for (Field field : fieldArr) {
                            hasValue += setColumnValue(obj, field, rs, colTotal);
                        }
                        // hasValue计数大于0，说明有字段赋给了当前的obj
                        if (hasValue > 0) {
                            result.add(obj);
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
                return result;
            }

            private int setColumnValue(Object obj, Field field, ResultSet rs, int colTotal) throws Exception {
                String name  = field.getName();
                Object value = null;
                for (int i = 1; i <= colTotal; i++) {
                    if (metaData.getColumnLabel(i).equalsIgnoreCase(name)) {
                        try {
                            value = rs.getObject(name);
                        } catch (Exception ignored) {

                        }
                        BeanUtil.setValue(field, obj, value);
                        return 1;
                    }
                }
                return 0;
            }

            /**
             * 获得除Object之外的所有父类和自己的属性数组
             *
             * @return 返回成员值
             */
            private Field[] getFields() {
                Class<?>    tmp  = pClass;
                List<Field> list = new ArrayList<>();
                while (!tmp.equals(Object.class)) {
                    list.addAll(Arrays.asList(tmp.getDeclaredFields()));
                    tmp = tmp.getSuperclass();
                }
                return list.toArray(new Field[]{});
            }

        });
    }

    /**
     * 查询单个数字结果
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 查询结果，无数据时返回0
     */
    public Number queryNumber(String pSQL, Object[] pParams) {
        ready();

        Number value = 0;
        try {
            value = mTemplate.queryForObject(pSQL, pParams, Number.class);
        } catch (EmptyResultDataAccessException ignored) {}
        return value;
    }

    /**
     * 查询单个字符串结果
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 查询结果，无数据时返回空字符串
     */
    public String queryString(String pSQL, Object[] pParams) {
        ready();

        String value = "";
        try {
            value = mTemplate.queryForObject(pSQL, pParams, String.class);
        } catch (EmptyResultDataAccessException ignored) {}
        return value;
    }

    /**
     * 查询单列数据
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @param pClass 列表元素类型，一般为pojo类
     * @return 结果集
     */
    public <T> List<T> queryColumn(Class<T> pClass, String pSQL, Object[] pParams) {
        ready();

        List<T> result;
        try {
            result = mTemplate.queryForList(pSQL, pParams, pClass);
        } catch (EmptyResultDataAccessException e) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 执行SQL语句
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 是否成功
     */
    public boolean execute(final String pSQL, final Object[] pParams) {
        ready();

        Boolean success = mTemplate.execute(connection -> {
            PreparedStatement ps = connection.prepareStatement(pSQL);
            int               i  = 1;
            if (pParams != null) {
                for (Object arg : pParams) {
                    ps.setObject(i++, arg);
                }
            }
            return ps;
        }, (PreparedStatementCallback<Boolean>) ps -> {
            boolean result = ps.execute();
            return result || ps.getUpdateCount() != -1;
        });
        return success == null ? false : success;
    }

    /**
     * 批处理执行SQL语句
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 受影响的数量，可能会出现单个语句返回-2( 驱动无法判断具体行数 )的情况，这种情况视受影响为1
     */
    public int executeBatch(final String pSQL, final List<Object[]> pParams){
        ready();

        int[] effects = mTemplate.batchUpdate(pSQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                Object[] args = pParams.get(index);
                if (args != null) {
                    int i = 1;
                    for (Object arg : args) {
                        ps.setObject(i++, arg);
                    }
                }
            }
            @Override
            public int getBatchSize() {
                return pParams != null ? pParams.size() : 0;
            }
        });

        int row = 0;
        for (int effect : effects) {
            row += effect == -2 ? 1 : effect;
        }
        return row;
    }

    /**
     * 批处理执行SQL语句
     * @param pSQL PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @param pArgTypes 与?对应的值的类型
     * @return 受影响的数量，可能会出现单个语句返回-2( 驱动无法判断具体行数 )的情况，这种情况视受影响为1
     */
    public int executeBatch(final String pSQL, final List<Object[]> pParams, final int[] pArgTypes){
        ready();

        int[] effects = mTemplate.batchUpdate(pSQL, pParams, pArgTypes);

        int row = 0;
        for (int effect : effects) {
            row += effect == -2 ? 1 : effect;
        }
        return row;
    }

    /**
     * 批处理执行SQL语句
     * @param pSQLs 多条完整的SQL语句
     * @return 受影响的数量，可能会出现单个语句返回-2( 驱动无法判断具体行数 )的情况，这种情况视受影响为1
     */
    public int executeBatch(final String[] pSQLs){
        int[] effects = mTemplate.batchUpdate(pSQLs);

        int row = 0;
        for (int effect : effects) {
            row += effect == -2 ? 1 : effect;
        }
        return row;
    }

    /**
     * 字符串处理，防止注入
     * @param pString 字符串
     * @param pNeedQuote 是否需要单引号
     * @return 处理结果
     */
    public String quote(String pString, boolean pNeedQuote){
        return SQLBuilder.quote(pString, pNeedQuote);
    }


    private void ready(){
        if(mTemplate == null){
            throw new RuntimeException("DataSource or JdbcTemplate not initialed!");
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
