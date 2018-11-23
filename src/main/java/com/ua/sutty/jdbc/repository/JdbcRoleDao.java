package com.ua.sutty.jdbc.repository;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.domain.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class JdbcRoleDao extends AbstractJdbcDao implements RoleDao {

    private Connection connection;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    public JdbcRoleDao() {
    }

    private static JdbcRoleDao instance;

    public JdbcRoleDao(BasicDataSource dataSource) {
        super(dataSource);
    }

    public static JdbcRoleDao instance() {
        if (instance == null) {
            instance = new JdbcRoleDao();
        }
        return instance;
    }

    private static final String INSERT_ROLE = String.format("INSERT INTO roles(%s)" +
        " VALUES (?);", Role.NAME);

    private static final String DELETE_ROLE = String.format("DELETE FROM roles WHERE %s = ?;", Role.ID);

    private static final String DELETE_USER_WITH_ROLE = String.format("DELETE FROM users WHERE %s = ?;", User.ROLE_ID);

    private static final String GET_ROLE_BY_NAME = "SELECT * FROM roles WHERE name = ?";

    private static final String UPDATE_ROLE = String.format("UPDATE roles SET %s = ?" +
        "WHERE id = ?", Role.NAME);

    public void create(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        try {
            Role roleInBase = findByName(role.getName());
            connection = super.createConnection();
            if (roleInBase.getName() != null) {
                if (roleInBase.getName().equals(role.getName())) {
                    System.out.println("This role already exist");
                    return;
                }
            }
            pst = connection.prepareStatement(INSERT_ROLE);
            pst.setString(1, role.getName());
            pst.execute();
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    public void update(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(UPDATE_ROLE);
            pst.setString(1, role.getName());
            pst.setLong(2, role.getId());
            pst.execute();
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    public void remove(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(DELETE_USER_WITH_ROLE);
            pst.setLong(1, role.getId());
            pst.execute();
            pst = connection.prepareStatement(DELETE_ROLE);

            pst.setLong(1, role.getId());
            int result = pst.executeUpdate();
            if (result == 0) {
                connection.rollback();
                throw new IllegalArgumentException();
            }
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    public Role findByName(String name) {
        if (name == null){
            throw new NullPointerException();
        }
        Role role = new Role();
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(GET_ROLE_BY_NAME);
            pst.setString(1, name);
            rs = pst.executeQuery();

            while (rs.next()) {
                role.setId(rs.getLong(Role.ID));
                role.setName(rs.getString(Role.NAME));
            }
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closeResultSet(rs);
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
        return role;
    }

    private void rollBackTransactional(Connection connection) {
        synchronized (this) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
