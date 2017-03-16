package com.prodaas.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by guyu on 2016/10/19.
 */
@Configuration
@MapperScan("com.prodaas.mapper")
public class MybatisConfig {
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig("/mysql.properties");
        HikariDataSource ds = new HikariDataSource(config);

        return ds;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean =
                new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        // 类型别名从这个包里面找
        sqlSessionFactoryBean.setTypeAliasesPackage("com.prodaas.model");
        // 其他配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(configuration);


        return sqlSessionFactoryBean.getObject();
    }
}
