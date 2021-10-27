package base.service.frameworks.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hxz on 2021/10/27 15:40.
 */

public class ShardingJdbc {

    public void init() throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置主库
        HikariDataSource masterDataSource = new HikariDataSource();
        masterDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        masterDataSource.setJdbcUrl("jdbc:mysql://localhost:7706/base_user");
        masterDataSource.setUsername("root");
        masterDataSource.setPassword("123456");
        dataSourceMap.put("ds_master", masterDataSource);

        // 配置第一个从库
        HikariDataSource slaveDataSource1 = new HikariDataSource();
        slaveDataSource1.setDriverClassName("com.mysql.jdbc.Driver");
        slaveDataSource1.setJdbcUrl("jdbc:mysql://localhost:7707/base_user");
        slaveDataSource1.setUsername("root");
        slaveDataSource1.setPassword("123456");
        dataSourceMap.put("ds_slave0", slaveDataSource1);

        // 配置读写分离规则
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration("ds_master_slave", "ds_master", Arrays.asList("ds_slave0"));

        // 获取数据源对象
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig, new Properties());

    }

}
