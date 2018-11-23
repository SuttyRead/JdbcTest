package com.ua.sutty.jdbc.utils;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private BasicDataSource basicDataSource;
    private static DataSource dataSource;

    private DataSource() {
    }

    public void properties() {
        Properties prop = getProperties();
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(prop.getProperty("db.driver"));
        basicDataSource.setUrl(prop.getProperty("db.url"));
        basicDataSource.setDefaultAutoCommit(Boolean.valueOf("db.autocommit"));
    }

    public static DataSource getInstance() {
        DataSource localInstance = dataSource;
        if (localInstance == null) {
            synchronized (DataSource.class) {
                localInstance = dataSource;
                if (localInstance == null) {
                    dataSource = localInstance = new DataSource();
                    dataSource.properties();
                }
            }
        }
        return localInstance;
    }

    public Connection getConnection() {
        Connection connection;
        try {
            connection = this.basicDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream("jdbc.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
