package com.ua.sutty.jdbc.utils;


import java.sql.*;


public class CreateTable {

    private Connection connection;
    private Statement stmt = null;

    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (\n" +
        "\tid bigint(11) PRIMARY KEY AUTO_INCREMENT,\n" +
        "\tlogin varchar(45) NOT NULL,\n" +
        "\tpassword varchar(45) NOT NULL,\n" +
        "\temail varchar(45) NOT NULL,\n" +
        "\tfirst_name varchar(45) NOT NULL,\n" +
        "\tlast_name varchar(45) NOT NULL,\n" +
        "\tbirthday date(45) NOT NULL,\n" +
        "\trole_id bigint(11)\n" +
        ");";

    private static final String CREATE_ROLES_TABLE = "CREATE TABLE IF NOT EXISTS roles (\n" +
        "\tid bigint(11) PRIMARY KEY AUTO_INCREMENT,\n" +
        "\tname varchar(45) NOT NULL UNIQUE,\n" +
        ");";

    private static final String ALTER_TABLE_USERS = "ALTER TABLE users ADD FOREIGN KEY(role_id) REFERENCES roles(id);";

    private void createTableIfNotExists(String name) {
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(name);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.getInstance().closeStatement(stmt);
            ConnectionFactory.getInstance().closeConnection(connection);
        }
    }

    public void createTableRoles() {
        createTableIfNotExists(CREATE_ROLES_TABLE);
    }

    public void createTableUsers() {
        createTableIfNotExists(CREATE_USERS_TABLE);
        createTableIfNotExists(ALTER_TABLE_USERS);
    }

}
