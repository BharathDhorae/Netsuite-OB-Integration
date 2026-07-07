package com.promanatia.CamelDemo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LoggingDatabaseConfig {

    @Bean(name = "loggingDataSource")
    @ConfigurationProperties(prefix = "logging.datasource")
    public DataSource loggingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "loggingJdbcTemplate")
    public JdbcTemplate loggingJdbcTemplate(
            @Qualifier("loggingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}