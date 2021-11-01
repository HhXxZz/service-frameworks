package org.hxz.service.frameworks.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
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

    private static final String CPU_CORE               = "%s.CPU.Core";
    private static final String BASE_PASSWORD          = "%s.BASE.Password";
    private static final String BASE_USERNAME          = "%s.BASE.Username";
    private static final String BASE_CONNECTION_URL    = "%s.BASE.ConnectionURL";
    private static final String BASE_DRIVER            = "%s.BASE.Driver";
    private static final String BASE_MySQLWaitTimeout  = "%s.BASE.MySQLWaitTimeout";
    private static final String BASE_MySQLSpindleCount = "%s.BASE.MySQLSpindleCount";

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
    //private HikariDataSource ds         = new HikariDataSource();
    private DataSource dataSource;
    private JdbcTemplate jdbc;


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
//                ins = ConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties");
//                Properties pro = new Properties();
//                if (ins != null) {
//                    pro.load(ins);
//                } else {
//                    LOG.error("MISSING : db.cfg.properties !");
//                    mInitialed.set(false);
//                    pro.load(new StringReader(""));
//                }
//                List<String> sources = GsonUtil.listFromJson(pro.getProperty("sources","[]"),String[].class);
//                if(sources.isEmpty()){
//                    LOG.error("MISSING : sources !");
//                    mInitialed.set(false);
//                    pro.load(new StringReader(""));
//                }
//                List<DataSource> list = buildDataSource(sources,pro);
//                if(list.size() == 1){
//                    //单个数据库连接
//                    dataSource = list.get(0);
//                }else{
//                    //多个数据库连接
//                    dataSource = buildMasterSlaveDataSource(sources,list);
//                }


                File file = new File(Objects.requireNonNull(ConnectionUtil.class.getClassLoader().getResource("db.yaml")).getPath());
                if(!file.exists()){
                    LOG.error("MISSING : db.properties !");
                    return;
                }
                dataSource = YamlShardingSphereDataSourceFactory.createDataSource(file);
                jdbc = new JdbcTemplate(dataSource);
            } catch (IOException ex) {
                LOG.error( "Parsing Error : db.cfg.properties !",ex);
            } catch (SQLException e){
                LOG.error( "SQL Error :  !",e);
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

    private List<DataSource> buildDataSource(List<String> sources,Properties pro){
        List<DataSource> list = new ArrayList<>();
        for(String source:sources) {
            //masterDataSource
            String base_driver = pro.getProperty(String.format(BASE_DRIVER,source), DEFAULT_BASE_DRIVER);
            String base_url = pro.getProperty(String.format(BASE_CONNECTION_URL,source), DEFAULT_BASE_CONNECTION_URL);
            String base_user = pro.getProperty(String.format(BASE_USERNAME,source), DEFAULT_BASE_USERNAME);
            String base_password = pro.getProperty(String.format(BASE_PASSWORD,source), DEFAULT_BASE_PASSWORD);
            int base_wait_timeout = Integer.parseInt(pro.getProperty(String.format(BASE_MySQLWaitTimeout,source), DEFAULT_BASE_MySQLWaitTimeout));
            int cpu_core = Integer.parseInt(pro.getProperty(String.format(CPU_CORE,source), DEFAULT_CPU_CORE));
            int spindle_count = Integer.parseInt(pro.getProperty(String.format(BASE_MySQLSpindleCount,source), DEFAULT_BASE_MySQLSpindleCount));
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setDriverClassName(base_driver);
            dataSource.setJdbcUrl(base_url);
            dataSource.setUsername(base_user);
            dataSource.setPassword(base_password);
            // Less than MySQL wait_timeout (show variables like '%timeout%';)
            dataSource.setMaxLifetime((base_wait_timeout - 60) * 1000);
            // ds.setIdleTimeout((base_wait_timeout - 61) * 1000); // MaxLifetime - IdleTimeout > 1000
            // connections = ((core_count * 2) + effective_spindle_count)
            dataSource.setMaximumPoolSize(cpu_core * 2 + spindle_count);
            list.add(dataSource);
        }
        return list;
    }

    private DataSource buildMasterSlaveDataSource(List<String> sourceNameList,List<DataSource> sourceList) throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        String master = "";
        List<String> slaveSourceName = new ArrayList<>();
        for(int i=0;i<sourceList.size();i++) {
            String tempName = sourceNameList.get(i);
            Map<String, DataSource> temp = new HashMap<>();
            dataSourceMap.put(tempName,sourceList.get(i));
            if(i == 0){
                master = tempName;
            }else{
                slaveSourceName.add(tempName);
            }
        }

        Properties pros = new Properties();
        pros.setProperty("sql.show","true");

        // 读写数据源配置
        ReadwriteSplittingDataSourceRuleConfiguration dataSourceRuleConfiguration = new ReadwriteSplittingDataSourceRuleConfiguration(
                        "master_slave_db", null, master, slaveSourceName, "ROUND_ROBIN");

        // 读写分离配置
        ReadwriteSplittingRuleConfiguration readwriteSplittingRuleConfiguration = new ReadwriteSplittingRuleConfiguration(
                        Collections.singleton(dataSourceRuleConfiguration), new HashMap<>());

        // 创建 ShardingSphereDataSource
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Collections.singleton(readwriteSplittingRuleConfiguration), pros);
    }


    private String getSourceName(Map<String,DataSource>map){
        return map.keySet().stream().iterator().next();
    }

    /**
     * 释放数据库，主要是关闭连接池
     */
    @SuppressWarnings("unused")
    public void release(){
        if(mInitialed.compareAndSet(true, false)) {
//            if (ds != null) {
//                ds.close();
//            }
        }
    }

    /**
     * 数据库连接
     * @return Connection Object
     * @throws SQLException Error
     */
    public Connection getConnection() throws SQLException {
        ready("getConnection");
        return dataSource.getConnection();
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
