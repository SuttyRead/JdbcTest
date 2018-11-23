package com.ua.sutty.jdbc;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.repository.JdbcRoleDao;
import com.ua.sutty.jdbc.repository.JdbcUserDao;
import com.ua.sutty.jdbc.repository.RoleDao;
import com.ua.sutty.jdbc.repository.UserDao;
import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class JdbcDataBaseTest {
    private static String JDBC_DRIVER;
    private static String JDBC_URL;
    private static String SCHEMA;
    private static String DATA_SET;
    @BeforeClass
    public static void initAndCreateSchema() throws Exception {
        Properties properties = new Properties();
        Path propertiesLocation = Paths.get("jdbc.properties");
        try (InputStream stream = Files.newInputStream(propertiesLocation)) {
            properties.load(stream);
        }

        JDBC_DRIVER = properties.getProperty("db.driver");
        JDBC_URL = properties.getProperty("db.url");
        SCHEMA = properties.getProperty("db.schema");
        DATA_SET = properties.getProperty("db.data_set");

    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = readDataSet();
        cleanlyInsertDataset(dataSet);
    }

    private DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(JDBC_URL);
        return dataSource;
    }

    private IDataSet readDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File(DATA_SET));
    }

    private void cleanlyInsertDataset(IDataSet dataSet) throws Exception {
        IDatabaseTester databaseTester = new JdbcDatabaseTester(
            JDBC_DRIVER, JDBC_URL);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test
    public void RoleFindByNameTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role role = roleDao.findByName("Manager");
        Assert.assertNotNull("Must returned the not null value on the existed data!", role);

        Assert.assertEquals("Id's must be equals", new Long(3L), role.getId());
        Assert.assertEquals("Names must be equals", "Manager", role.getName());

        role = roleDao.findByName("NotExistsName");

        Assert.assertNull("Must returned the not null value", role);
    }

    @Test(expected = NullPointerException.class)
    public void RoleFindByNameFromStringTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        roleDao.findByName(null);
    }

    @Test
    public void RoleCreateFromObjectTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role source = new Role();
        source.setName("Teacher");

        roleDao.create(source);

        Role role = roleDao.findByName(source.getName());

        Assert.assertNotNull("Must returned the not null value after created object", role);

        Assert.assertEquals("Names must be equals", source.getName(), role.getName());
    }

    @Test(expected = NullPointerException.class)
    public void RoleCreateFromNullObjectTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        roleDao.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RoleCreateFromObjectWithIdTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role role = new Role(1L, "Student");
        roleDao.create(role);
    }

    @Test
    public void RoleUpdateFromObjectTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role source = new Role();
        source.setId(1L);
        source.setName("Super-Admin");

        roleDao.update(source);

        Role role = roleDao.findByName(source.getName());

        Assert.assertNotNull("Must returned the not null value on the existed data!", role);

        Assert.assertEquals("Names must be equals", source.getName(), role.getName());

        role = roleDao.findByName("Admin");

        Assert.assertNull("The user with name 'Admin' must be replaced by 'Super-Admin'",
            role);
    }

    @Test(expected = NullPointerException.class)
    public void RoleUpdateFromNullObjectTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        roleDao.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RoleUpdateFromObjectWithoutIdTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role role = new Role(null, "Student");
        roleDao.update(role);
    }

    @Test
    public void RoleRemoveObjectFromDBTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role source = new Role();
        source.setId(2L);
        source.setName("User");

        roleDao.remove(source);

        Role role = roleDao.findByName(source.getName());

        Assert.assertNull("Must returned the not null value after deleted object", role);
    }

    @Test(expected = NullPointerException.class)
    public void RoleRemoveFromNullObjectTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        roleDao.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RoleRemoveFromObjectWithoutIdTest() {
        RoleDao roleDao = new JdbcRoleDao((BasicDataSource) dataSource());
        Role role = new Role(null, "Student");
        roleDao.remove(role);
    }

    @Test
    public void UserFindByLoginTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());

        User user = userDao.findByLogin("user_2");
        User expectedUser = new User(2L, "user_2", "1234", "finy@db.com",
            "Benjamin", "Franklin", Date.valueOf("1982-03-13"), 2L);

        Assert.assertEquals("Should be equal!", expectedUser, user);

        user = userDao.findByLogin("NotExistsName");

        Assert.assertNull("Must returned the not null value", user);
    }

    @Test(expected = NullPointerException.class)
    public void UserFindByLoginFromStringTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        userDao.findByLogin(null);
    }

    @Test
    public void UserFindByEmailTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());

        User user = userDao.findByEmail("finy@db.com");
        User expectedUser = new User(2L, "user_2", "1234", "finy@db.com",
            "Benjamin", "Franklin", Date.valueOf("1982-03-13"), 2L);

        Assert.assertEquals("Should be equal!", expectedUser, user);

        user = userDao.findByEmail("NotExistsEmail");

        Assert.assertNull("Must returned the not null value", user);
    }

    @Test(expected = NullPointerException.class)
    public void UserFindByEmailFromStringTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        userDao.findByEmail(null);
    }

    @Test
    public void UsersFindAllTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        List<User> expectedUsers = new LinkedList<>();
        User expectedUser = new User(1L, "user_1", "1234", "user@db.com",
            "Bob", "Fincher", Date.valueOf("1974-01-15"), 1L);
        expectedUsers.add(expectedUser);

        expectedUser = new User(2L, "user_2", "1234", "finy@db.com",
            "Benjamin", "Franklin", Date.valueOf("1982-03-13"), 2L);
        expectedUsers.add(expectedUser);

        expectedUser = new User(3L, "user_3", "1234", "tiny@db.com",
            "Mary", "Jay", Date.valueOf("1996-11-25"), 2L);
        expectedUsers.add(expectedUser);


        List<User> users = userDao.findAll();

        Assert.assertEquals("Should be equal lists of users!",
            expectedUsers, users);
    }

    @Test
    public void UserCreateFromObjectTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User source = new User(null, "user_4", "1234", "ziny@db.com",
            "Mark", "Engels", Date.valueOf("1832-03-13"), 2L);

        userDao.create(source);

        User user = userDao.findByLogin(source.getLogin());

        Assert.assertNotNull("Must returned the not null value after created object", user);

        Assert.assertEquals("Users must be equals", source, user);
    }

    @Test(expected = NullPointerException.class)
    public void UserCreateFromNullObjectTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        userDao.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void UserCreateFromObjectWithIdTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User user = new User(1L, "user_4", "1234", "ziny@db.com",
            "Mark", "Engels", Date.valueOf("1832-03-13"), 2L);
        userDao.create(user);
    }

    @Test
    public void UserUpdateFromObjectTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User source = new User(2L, "user_2", "1234", "finy@db.com",
            "Benjamin", "Franklin", Date.valueOf("1982-03-13"), 2L);

        source.setFirstName("Jo");
        source.setLastName("Mayao");
        source.setRoleId(1L);
        source.setLogin("user_test");
        userDao.update(source);

        User user = userDao.findByLogin(source.getLogin());

        Assert.assertNotNull("Must returned the not null value on the existed data!", user);

        Assert.assertEquals("Users must be equals", source, user);

        user = userDao.findByLogin("user_2");

        Assert.assertNull("The user with name 'user_2' must be replaced by 'user_test'",
            user);
    }

    @Test(expected = NullPointerException.class)
    public void UserUpdateFromNullObjectTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        userDao.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void UserUpdateFromObjectWithIdTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User user = new User(null, "user_4", "1234", "ziny@db.com",
            "Mark", "Engels", Date.valueOf("1832-03-13"), 2L);
        userDao.update(user);
    }

    @Test
    public void UserRemoveObjectFromDBTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User source = new User(2L, "user_2", "1234", "finy@db.com",
            "Benjamin", "Franklin", Date.valueOf("1982-03-13"), 2L);

        userDao.remove(source);

        User user = userDao.findByLogin(source.getLogin());

        Assert.assertNull("Must returned the not null value after deleted object", user);
    }

    @Test(expected = NullPointerException.class)
    public void UserRemoveFromNullObjectTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        userDao.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void UserRemoveFromObjectWithIdTest() {
        UserDao userDao = new JdbcUserDao((BasicDataSource) dataSource());
        User user = new User(null, "user_4", "1234", "ziny@db.com",
            "Mark", "Engels", Date.valueOf("1832-03-13"), 2L);
        userDao.remove(user);
    }
}
