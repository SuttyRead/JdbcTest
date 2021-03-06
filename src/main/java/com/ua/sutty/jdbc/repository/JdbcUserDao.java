package com.ua.sutty.jdbc.repository;

import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.utils.ConnectionFactory;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDao extends AbstractJdbcDao implements UserDao {

    private Connection connection;
    private PreparedStatement pst = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public JdbcUserDao() {
    }

    public JdbcUserDao(BasicDataSource basicDataSource) {
        super(basicDataSource);
    }


    private static JdbcUserDao instance;

    public static JdbcUserDao instance() {
        if (instance == null) {
            instance = new JdbcUserDao();
        }
        return instance;
    }

    private static final String INSERT_USER = String.format("INSERT INTO users(%s, %s, %s, %s, %s, %s, %s)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?);", User.LOGIN, User.PASSWORD, User.EMAIL, User.FIRST_NAME, User.LAST_NAME, User.BIRTHDAY, User.ROLE_ID);

    private static final String GET_ALL_USERS = "SELECT * FROM users";

    private static final String DELETE_ADMINISTRATOR_BY_ID = String.format("DELETE FROM users WHERE %s = ?;", User.ID);

    private static final String GET_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";

    private static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";

    private static final String UPDATE_USER = String.format("UPDATE users SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? " +
        "WHERE id = ?", User.LOGIN, User.PASSWORD, User.EMAIL, User.FIRST_NAME, User.LAST_NAME, User.BIRTHDAY, User.ROLE_ID);


    public void create(User user) {
        if (user == null){
            throw new NullPointerException();
        }
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(INSERT_USER);
            pst.setString(1, user.getLogin());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getFirstName());
            pst.setString(5, user.getLastName());
            pst.setDate(6, user.getBirthday());
            pst.setLong(7, user.getRoleId());
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

    public void update(User user) {
        if (user == null){
            throw new NullPointerException();
        }
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            pst = connection.prepareStatement(UPDATE_USER);
            pst.setString(1, user.getLogin());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getFirstName());
            pst.setString(5, user.getLastName());
            pst.setDate(6, user.getBirthday());
            pst.setLong(7, user.getRoleId());
            pst.setLong(8, user.getId());

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

    public void remove(User user) {
        if (user == null){
            throw new NullPointerException();
        }
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            pst = connection.prepareStatement(DELETE_ADMINISTRATOR_BY_ID);
            pst.setLong(1, user.getId());
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

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(GET_ALL_USERS);
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong(User.ID));
                user.setLogin(rs.getString(User.LOGIN));
                user.setPassword(rs.getString(User.PASSWORD));
                user.setEmail(rs.getString(User.EMAIL));
                user.setFirstName(rs.getString(User.FIRST_NAME));
                user.setLastName(rs.getString(User.LAST_NAME));
                user.setBirthday(rs.getDate(User.BIRTHDAY));
                user.setRoleId(rs.getLong(User.ROLE_ID));
                users.add(user);
            }
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closeResultSet(rs);
            super.closeStatement(stmt);
            super.closeConnection(connection);
        }
        return users;
    }

    public User findByLogin(String login) {
        if (login == null){
            throw new NullPointerException();
        }
        User user = new User();
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            pst = connection.prepareStatement(GET_USER_BY_LOGIN);
            pst.setString(1, login);
            rs = pst.executeQuery();
            while (rs.next()) {
                user.setId(rs.getLong(User.ID));
                user.setLogin(rs.getString(User.LOGIN));
                user.setPassword(rs.getString(User.PASSWORD));
                user.setEmail(rs.getString(User.EMAIL));
                user.setFirstName(rs.getString(User.FIRST_NAME));
                user.setLastName(rs.getString(User.LAST_NAME));
                user.setBirthday(rs.getDate(User.BIRTHDAY));
                user.setRoleId(rs.getLong(User.ROLE_ID));
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
        return user;
    }

    public User findByEmail(String email) {
        if (email == null){
            throw new NullPointerException();
        }
        User user = new User();
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            pst = connection.prepareStatement(GET_USER_BY_EMAIL);
            pst.setString(1, email);
            rs = pst.executeQuery();
            while (rs.next()) {
                user.setId(rs.getLong(User.ID));
                user.setLogin(rs.getString(User.LOGIN));
                user.setPassword(rs.getString(User.PASSWORD));
                user.setEmail(rs.getString(User.EMAIL));
                user.setFirstName(rs.getString(User.FIRST_NAME));
                user.setLastName(rs.getString(User.LAST_NAME));
                user.setBirthday(rs.getDate(User.BIRTHDAY));
                user.setRoleId(rs.getLong(User.ROLE_ID));
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
        return user;
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
