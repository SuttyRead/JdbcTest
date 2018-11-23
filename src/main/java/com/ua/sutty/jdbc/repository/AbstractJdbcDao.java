package com.ua.sutty.jdbc.repository;

import com.ua.sutty.jdbc.utils.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class AbstractJdbcDao {

    private BasicDataSource basicDataSource;
    private static AbstractJdbcDao dataSource;

    public AbstractJdbcDao() {
    }

    public AbstractJdbcDao(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }


    private void properties() {
        Properties prop = getProperties();
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(prop.getProperty("db.driver"));
        basicDataSource.setUrl(prop.getProperty("db.url"));
        basicDataSource.setDefaultAutoCommit(Boolean.valueOf("db.autocommit"));
    }

    public static AbstractJdbcDao getInstance() {
        AbstractJdbcDao localInstance = dataSource;
        if (localInstance == null) {
            synchronized (DataSource.class) {
                localInstance = dataSource;
                if (localInstance == null) {
                    dataSource = localInstance = new AbstractJdbcDao();
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



    public Connection createConnection() {
        return DataSource.getInstance().getConnection();
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
