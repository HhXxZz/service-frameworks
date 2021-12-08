package org.hxz.service.frameworks.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.google.common.io.ByteStreams;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.JdbcType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.hxz.service.frameworks.utils.ConnectionUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Created by hxz on 2021/11/29 16:48.
 */

@Configuration
public class DbConfig {
    private static final Logger logger = LogManager.getLogger(DbConfig.class);

    @Bean
    public DataSource getDataSource(){
        try {
            InputStream inputStream = DbConfig.class.getClassLoader().getResourceAsStream("db.yml");
            if(inputStream == null){
                logger.error("MISSING : db.yml !");
                return null;
            }
//            String path = url.getPath();
//            logger.info("DataSource.path="+path);
//            File file = new File(path);
//            if(!file.exists()){
//                logger.error("MISSING : db.yml !");
//                return null;
//            }
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return YamlShardingSphereDataSourceFactory.createDataSource(bytes);
        }catch (Exception e){
            logger.error(e);
        }
        return null;
    }

    // 最新版
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        //sqlSessionFactoryBean.setPlugins(new Interceptor[]{offsetLimitInterceptor()});
        Interceptor[] plugins = new Interceptor[]{mybatisPlusInterceptor()};
        sqlSessionFactoryBean.setPlugins(plugins);
        sqlSessionFactoryBean.setDataSource(getDataSource());
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));


        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(false);//开启下划线转驼峰
        sqlSessionFactoryBean.setConfiguration(configuration);

        return sqlSessionFactoryBean;
    }



}
