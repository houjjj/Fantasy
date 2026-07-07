package com.houjun.clickhouse.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class ClickHouseConfig {

    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}