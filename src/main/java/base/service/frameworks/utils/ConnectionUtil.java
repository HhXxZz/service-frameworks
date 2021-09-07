package base.service.frameworks.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 
 * 用来获取数据库连接的基础工具类，需要与db.cfg.properties文件配合使用
 *
 */
@SuppressWarnings("unused")
public enum  ConnectionUtil {
    INSTANCE;
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(ConnectionUtil.class);

    private static final String CPU_CORE               = "CPU.Core";
    private static final String BASE_PASSWORD          = "BASE.Password";
    private static final String BASE_USERNAME          = "BASE.Username";
    private static final String BASE_CONNECTION_URL    = "BASE.ConnectionURL";
    private static final String BASE_DRIVER            = "BASE.Driver";
    private static final String BASE_MySQLWaitTimeout  = "BASE.MySQLWaitTimeout";
    private static final String BASE_MySQLSpindleCount = "BASE.MySQLSpindleCount";

    private static final String DEFAULT_BASE_PASSWORD          = "root";
    private static final String DEFAULT_BASE_USERNAME          = "sd-9898w";
    private static final String DEFAULT_BASE_CONNECTION_URL    = "jdbc:mysql://127.0.0.1:3306/test_user?autoReconnect=true&autoReconnectForPools=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true&useAffectedRows=true";
    private static final String DEFAULT_BASE_DRIVER            = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_CPU_CORE               = "4";
    private static final String DEFAULT_BASE_MySQLWaitTimeout  = "86400";
    private static final String DEFAULT_BASE_MySQLSpindleCount = "1";

    // ===========================================================
    // Fields
    // ===========================================================
    private AtomicBoolean    mInitialed = new AtomicBoolean(false);
    private HikariDataSource ds         = new HikariDataSource();
    private JdbcTemplate     jdbc       = new JdbcTemplate(ds);

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    /**
     * 加载数据库配置信息 
     */
    public void init(){
        if(mInitialed.compareAndSet(false, true)) {
            InputStream ins = null;
            try {
                ins = ConnectionUtil.class.getClassLoader().getResourceAsStream("db.cfg.properties");
                Properties pro = new Properties();
                if (ins != null) {
                    pro.load(ins);
                } else {
                    LOG.error("MISSING : db.cfg.properties !");
                    mInitialed.set(false);
                    pro.load(new StringReader(""));
                }
                String base_driver       = pro.getProperty(BASE_DRIVER, DEFAULT_BASE_DRIVER);
                String base_url          = pro.getProperty(BASE_CONNECTION_URL, DEFAULT_BASE_CONNECTION_URL);
                String base_user         = pro.getProperty(BASE_USERNAME, DEFAULT_BASE_USERNAME);
                String base_password     = pro.getProperty(BASE_PASSWORD, DEFAULT_BASE_PASSWORD);
                int    base_wait_timeout = Integer.parseInt(pro.getProperty(BASE_MySQLWaitTimeout, DEFAULT_BASE_MySQLWaitTimeout));
                int    cpu_core          = Integer.parseInt(pro.getProperty(CPU_CORE, DEFAULT_CPU_CORE));
                int    spindle_count     = Integer.parseInt(pro.getProperty(BASE_MySQLSpindleCount, DEFAULT_BASE_MySQLSpindleCount));

                ds.setDriverClassName(base_driver);
                ds.setJdbcUrl(base_url);
                ds.setUsername(base_user);
                ds.setPassword(base_password);
                // Less than MySQL wait_timeout (show variables like '%timeout%';)
                ds.setMaxLifetime((base_wait_timeout - 60) * 1000);
                // ds.setIdleTimeout((base_wait_timeout - 61) * 1000); // MaxLifetime - IdleTimeout > 1000
                // connections = ((core_count * 2) + effective_spindle_count)
                ds.setMaximumPoolSize(cpu_core * 2 + spindle_count);
            } catch (IOException ex) {
                LOG.error( "Parsing Error : db.cfg.properties !",ex);
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        LOG.error("Close fail : db.cfg.properties !",e);
                    }
                }
            }
        }
    }

    /**
     * 释放数据库，主要是关闭连接池
     */
    @SuppressWarnings("unused")
    public void release(){
        if(mInitialed.compareAndSet(true, false)) {
            if (ds != null) {
                ds.close();
            }
        }
    }

    /**
     * 数据库连接
     * @return Connection Object
     * @throws SQLException Error
     */
    public Connection getConnection() throws SQLException {
        ready("getConnection");
        return ds.getConnection();
    }

    /**
     * 获取JdbcTemplate
     * @return JdbcTemplate Object
     */
    public JdbcTemplate getJDBC(){
        //ready("getJDBC");
        return jdbc;
    }

    /**
     * 关闭数据库连接
     * @param rs ResultSet
     * @param stmt Statement
     * @param con Connection
     */
    @SuppressWarnings({"SameParameterValue"})
    public void close(ResultSet rs, Statement stmt, Connection con) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    @SuppressWarnings(value = "SameParameterValue")
    private void ready(String pFunction){
        if(!mInitialed.get()){
            LOG.warn("DB configs unavailable! Using default [{}]", pFunction);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static void main(String[] args){
    }
}
