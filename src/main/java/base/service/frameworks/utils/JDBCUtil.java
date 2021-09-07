package base.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于简化数据库访问操作，该类线程安全
 *
 * @author Administrator
 */
@SuppressWarnings({"unused", "Convert2Lambda", "Anonymous2MethodRef", "WeakerAccess"})
public class JDBCUtil {
    private static final Logger LOG = LogManager.getLogger(JDBCUtil.class);

    /**
     * 获取列表
     *
     * @param pClass  列表元素类型，一般为pojo类
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 结果集
     */
    public static <T> List<T> queryList(final Class<T> pClass, final String pSql, final Object[] pParams) {
        return queryList(ConnectionUtil.INSTANCE.getJDBC(), pClass, pSql, pParams);
    }

    /**
     * 获取列表
     *
     * @param pJdbcTemplate 指定JdbcTemplate
     * @param pClass        列表元素类型，一般为pojo类
     * @param pSql          PreparedStatement类型sql语句，变量为?表示
     * @param pParams       与?对应的值
     * @return 结果集
     */
    public static <T> List<T> queryList(final JdbcTemplate pJdbcTemplate, final Class<T> pClass, final String pSql, final Object[] pParams) {
        // System.out.println("queryList sql: " + pSql);
        // if (pParams != null) {
        // System.out.println(Arrays.deepToString(pParams));
        // }
        return pJdbcTemplate.execute(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(pSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if (pParams != null) {
                    for (int i = 0; i < pParams.length; i++) {
                        ps.setObject(i + 1, pParams[i]);
                    }
                }
                return ps;
            }

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
                            BeanUtil.setValue(field, obj, value);
                        } catch (Exception e) {
                            LOG.error( "field[{}] value[{}][{}]", name, value, value != null ? value.getClass().getSimpleName() : "null",e);
                        }

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
     * 查询单个Pojo对象
     *
     * @param pClass  pojo类
     * @param pSql    PreparedStatement 类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 结果对象
     */
    public static <T> T queryForOne(final Class<T> pClass, final String pSql, final Object[] pParams) {
        return queryForOne(ConnectionUtil.INSTANCE.getJDBC(), pClass, pSql, pParams);
    }

    public static <T> T queryForOne(final JdbcTemplate pJdbcTemplate, final Class<T> pClass, final String pSql, final Object[] pParams) {
        List<T> result = queryList(pJdbcTemplate, pClass, pSql, pParams);
        T       obj    = null;
        if (result.size() > 0) {
            obj = result.get(0);
        }
        return obj;
    }

    /**
     * 插入或更新
     * insert on duplicate key update
     *
     * @param pSql          PreparedStatement类型sql语句，变量为?表示
     * @param pInsertValues 插入值
     * @param pUpdateValues 更新值
     * @return 是否成功
     */
    public static boolean insertOrUpdate(final String pSql, final Object[] pInsertValues, final Object[] pUpdateValues) {
        return insertOrUpdate(ConnectionUtil.INSTANCE.getJDBC(), pSql, pInsertValues, pUpdateValues);
    }

    public static boolean insertOrUpdate(final JdbcTemplate pJdbcTemplate, final String pSql, final Object[] pInsertValues, final Object[] pUpdateValues) {
        // System.out.println("更新sql: " + pSql);
        // if (pInsertValues != null) {
        //     System.out.println(Arrays.deepToString(pInsertValues));
        // }
        // if (pUpdateValues != null) {
        //     System.out.println(Arrays.deepToString(pUpdateValues));
        // }

        return pJdbcTemplate.execute(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(pSql);
                int               i  = 1;
                if (pInsertValues != null) {
                    for (Object arg : pInsertValues) {
                        ps.setObject(i++, arg);
                    }
                }
                if (pUpdateValues != null) {
                    for (Object arg : pUpdateValues) {
                        ps.setObject(i++, arg);
                    }
                }
                return ps;
            }

        }, new PreparedStatementCallback<Boolean>() {

            public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                return ps.execute();
            }

        });
    }

    /**
     * 插入返回主键值
     *
     * @param pSql          PreparedStatement类型sql语句，变量为?表示
     * @param pInsertValues 插入值
     * @return 返回主键
     */
    public static long insertForPrimaryKey(final String pSql, final Object[] pInsertValues) {
        return insertForPrimaryKey(ConnectionUtil.INSTANCE.getJDBC(), pSql, pInsertValues);
    }

    public static long insertForPrimaryKey(final JdbcTemplate pJdbcTemplate, final String pSql, final Object[] pInsertValues) {
        // System.out.println("插入sql: " + pSql);
        // if (pInsertValues != null) {
        //     System.out.println(Arrays.deepToString(pInsertValues));
        // }

        return pJdbcTemplate.execute(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(pSql, Statement.RETURN_GENERATED_KEYS);
                int               i  = 1;
                if (pInsertValues != null) {
                    for (Object arg : pInsertValues) {
                        ps.setObject(i++, arg);
                    }
                }
                return ps;
            }

        }, new PreparedStatementCallback<Long>() {

            public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                // 指定返回生成的主键

                ps.executeUpdate();
                // 检索由于执行此 Statement 对象而创建的所有自动生成的键
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    // System.out.println("数据主键：" + id);
                    return rs.getLong(1);
                }
                return (long) 0;
            }

        });
    }

    /**
     * 获取Long值
     *
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return long值
     */
    public static Long queryForLong(String pSql, Object[] pParams) {
        return queryForLong(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams);
    }

    public static Long queryForLong(final JdbcTemplate pJdbcTemplate, String pSql, Object[] pParams) {
        // System.out.println("queryForLong pSql: " + pSql);
        // if (pParams != null) {
        //     System.out.println(Arrays.deepToString(pParams));
        // }
        Long value;
        try {
            value = pJdbcTemplate.queryForObject(pSql, pParams, Long.class);
        } catch (EmptyResultDataAccessException e) {
            value = 0L;
        }
        return value;
    }

    /**
     * 获取int值
     *
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return int值
     */
    public static Integer queryForInt(String pSql, Object[] pParams) {
        return queryForInt(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams);
    }

    public static Integer queryForInt(final JdbcTemplate pJdbcTemplate, String pSql, Object[] pParams) {
        // System.out.println("queryForInt sql: " + pSql);
        // if (pParams != null) {
        //     System.out.println(Arrays.deepToString(pParams));
        // }
        Integer value;
        try {
            value = pJdbcTemplate.queryForObject(pSql, pParams, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            value = 0;
        }
        return value;
    }

    /**
     * 获取字符串，仅获取第一个column的值
     *
     * @param pSql 查询语句
     * @return String值
     */
    public static String getSingleString(String pSql) {
        return getSingleString(ConnectionUtil.INSTANCE.getJDBC(), pSql);
    }

    public static String getSingleString(final JdbcTemplate pJdbcTemplate, String pSql) {
        // System.out.println("getSingleString sql: " + pSql);
        final List<String> list = new ArrayList<>(1);
        pJdbcTemplate.query(pSql, new RowCallbackHandler() {

            public void processRow(ResultSet rs) throws SQLException {
                list.add(rs.getString(1));
            }
        });
        return list.size() < 1 ? null : list.get(0);
    }

    /**
     * 获取某列数据列表
     *
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @param pClass  列表元素类型，一般为pojo类
     * @return 某列值列表
     */
    public static <T> List<T> querySingleColumnRowList(String pSql, Object[] pParams, Class<T> pClass) {
        return querySingleColumnRowList(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams, pClass);
    }

    public static <T> List<T> querySingleColumnRowList(final JdbcTemplate pJdbcTemplate, String pSql, Object[] pParams, Class<T> pClass) {
        // System.out.println("querySingleColumnRowList sql: " + pSql);
        // if (pParams != null) {
        //     System.out.println(Arrays.deepToString(pParams));
        // }

        List<T> result;
        try {
            result = pJdbcTemplate.queryForList(pSql, pParams, pClass);
        } catch (EmptyResultDataAccessException e) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 获取第一条数据
     *
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @param pClass  pojo类
     * @return 第一条数据
     */
    public static <T> T querySingleColumnRow(String pSql, Object[] pParams, Class<T> pClass) {
        return querySingleColumnRow(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams, pClass);
    }

    public static <T> T querySingleColumnRow(final JdbcTemplate pJdbcTemplate, String pSql, Object[] pParams, Class<T> pClass) {
        // System.out.println("querySingleColumnRow sql: " + pSql);
        // if (pParams != null) {
        //     System.out.println(Arrays.deepToString(pParams));
        // }
        T obj = null;
        try {
            List<T> result = pJdbcTemplate.queryForList(pSql, pParams, pClass);
            if (result.size() > 0) {
                obj = result.get(0);
            }
        } catch (EmptyResultDataAccessException ignored) {
        }
        return obj;
    }

    /**
     * 执行SQL
     *
     * @param pSql    PreparedStatement类型sql语句，变量为?表示
     * @param pParams 与?对应的值
     * @return 执行结果
     */
    public static boolean execute(final String pSql, final Object[] pParams) {
        return execute(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams);
    }

    public static boolean execute(final JdbcTemplate pJdbcTemplate, final String pSql, final Object[] pParams) {
        // System.out.println("execute sql: " + pSql);
        // if (pParams != null) {
        //     System.out.println(Arrays.deepToString(pParams));
        // }

        return pJdbcTemplate.execute(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(pSql);
                int               i  = 1;
                if (pParams != null) {
                    for (Object arg : pParams) {
                        ps.setObject(i++, arg);
                    }
                }
                return ps;
            }

        }, new PreparedStatementCallback<Boolean>() {
            public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                boolean result = ps.execute();
                return result || ps.getUpdateCount() > 0;
            }

        });
    }

    /**
     * 批处理执行SQL语句
     *
     * @param pSql    sql语句
     * @param pParams 参数集合
     * @return 执行成功的条数
     */
    public static int executeBatch(final String pSql, final List<Object[]> pParams) {
        return executeBatch(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams);
    }

    public static int executeBatch(final JdbcTemplate pJdbcTemplate, final String pSql, final List<Object[]> pParams) {
        //LOG.dd("executeBatch size %d", pParams != null ? pParams.size() : 0);
        int[] effects = pJdbcTemplate.batchUpdate(pSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                Object[] args = pParams.get(index);
                //LOG.dd("index %d args.size = %d", index, args != null ? args.length : 0);
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
     * 批量插入数据 并且返回主键ID集合
     * @param pSql sql
     * @param pParams params
     * @return 主键集合
     */
    public static List<Long> insertBatch(final String pSql, final List<Object[]> pParams) throws SQLException {
        Connection        con  = null;
        PreparedStatement ps   = null;
        ResultSet         rs   = null; //获取结果
        List<Long>        list = null;
        try {
            con = ConnectionUtil.INSTANCE.getConnection();
            con.setAutoCommit(false);
            ps = con.prepareStatement(pSql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (Object[] params : pParams) {
                for (int i = 1; i <= params.length; i++) {
                    ps.setObject(i, params[i - 1]);
                }
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            rs = ps.getGeneratedKeys();
            list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getLong(1));//取得ID
            }
        } catch (SQLException e) {
            LOG.error("InsertBatch error.",e);
        } finally {
            if(con != null){
                con.close();
            }
            if(ps != null){
                ps.close();
            }
            if(rs != null){
                rs.close();
            }
        }
        return list;
    }

    /**
     * 批处理执行SQL语句
     *
     * @param pSql SQL集合
     * @return 执行成功的条数
     */
    public static int executeBatch(final String[] pSql) {
        return executeBatch(ConnectionUtil.INSTANCE.getJDBC(), pSql);
    }

    public static int executeBatch(final JdbcTemplate pJdbcTemplate, final String[] pSql) {
        int[] effects = pJdbcTemplate.batchUpdate(pSql);

        int row = 0;
        for (int effect : effects) {
            row += effect == -2 ? 1 : effect;
        }
        return row;
    }

    /**
     * 执行更新、插入语句
     *
     * @param pSql    SQL语句
     * @param pParams 参数集合
     * @return 受影响的条数
     */
    public static int update(final String pSql, final Object[] pParams) {
        return update(ConnectionUtil.INSTANCE.getJDBC(), pSql, pParams);
    }

    public static int update(final JdbcTemplate pJdbcTemplate, final String pSql, final Object[] pParams) {
        return pJdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(pSql);
                int               i  = 1;
                if (pParams != null) {
                    for (Object arg : pParams) {
                        ps.setObject(i++, arg);
                    }
                }
                return ps;
            }
        });
    }

    /**
     * 在自定义SQL时，用于生成字符型value
     * 会对特殊字符进行处理以防止SQL注入
     * 比如：value为 ABC\'你好\"\r\n 输出后为 'ABC\'你好"\r\n'
     *
     * @param pValue value值
     * @return 处理后的value值
     */
    public static String quoteValue(String pValue) {
        return quoteValue(pValue, true);
    }

    /**
     * 在自定义SQL时，用于生成字符型value
     * 会对特殊字符进行处理以防止SQL注入
     * 比如：value为 ABC\'你好\"\r\n 输出后为 'ABC\'你好"\r\n'
     *
     * @param pValue value值
     * @return 处理后的value值
     */
    public static String quoteValue(String pValue, boolean pNeedQuote) {
        if (pValue != null) {
            // NUL (0x00) --> \0
            // BS  (0x08) --> \b
            // TAB (0x09) --> \t
            // LF  (0x0a) --> \n
            // CR  (0x0d) --> \r
            // SUB (0x1a) --> \Z
            // "   (0x22) --> \"
            // '   (0x27) --> \'
            // \   (0x5c) --> \\
            StringBuilder result = new StringBuilder();
            if (pNeedQuote) {
                result.append("'");
            }
            char[] chars = pValue.toCharArray();
            for (char c : chars) {
                switch ((int) c) {
                    case 0x00: // NUL (0x00) --> \0
                        result.append("\\0");
                        break;
                    case 0x08: // BS  (0x08) --> \b
                        result.append("\\b");
                        break;
                    case 0x09: // TAB (0x09) --> \t
                        result.append("\\t");
                        break;
                    case 0x0a: // LF  (0x0a) --> \n
                        result.append("\\n");
                        break;
                    case 0x0d: // CR  (0x0d) --> \r
                        result.append("\\r");
                        break;
                    case 0x1a: // SUB (0x1a) --> \Z
                        result.append("\\Z");
                        break;
                    case 0x22: // "   (0x22) --> \"
                        result.append("\"");
                        break;
                    case 0x27: // '   (0x27) --> \'
                        result.append("\\\'");
                        break;
                    case 0x5c: // \   (0x5c) --> \\
                        result.append("\\\\");
                        break;
                    default:
                        result.append(c);
                }
            }
            if (pNeedQuote) {
                result.append("'");
            }
            return result.toString();
        }
        return "''";
    }
}
