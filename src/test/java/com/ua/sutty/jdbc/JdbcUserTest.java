package com.ua.sutty.jdbc;

import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.repository.JdbcUserDao;
import org.apache.xmlbeans.impl.piccolo.xml.EntityManager;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

public class JdbcUserTest extends DBUnitConfig {

    private JdbcUserDao jdbcUserDao = new JdbcUserDao();

    public JdbcUserTest(String name) {
        super(name);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        beforeData = new FlatXmlDataSetBuilder().build(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testDataSet.xml"));

        tester.setDataSet(beforeData);
        tester.onSetup();
    }

    @Test
    public void testGetAll() throws Exception {
        List<User> persons = jdbcUserDao.findAll();

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("com/devcolibri/entity/person/person-data.xml"));

        IDataSet actualData = tester.getConnection().createDataSet();
        Assertion.assertEquals(expectedData, actualData);
        Assert.assertEquals(expectedData.getTable("person").getRowCount(), persons.size());
    }

    @Test
    public void testSave() throws Exception {
        Date date = new Date(1999, 10, 8);
        Date birthday = new Date(1999, 10, 8);
        User user = User.builder()
            .login("user_4")
            .password("1234")
            .email("tiny@db.com")
            .firstName("Mary")
            .lastName("Jay")
            .birthday(birthday)
            .roleId(3L)
            .build();
        jdbcUserDao.create(user);

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testDataSet-save.xml"));

        IDataSet actualData = tester.getConnection().createDataSet();

        String[] ignore = {"id"};
        Assertion.assertEqualsIgnoreCols(expectedData, actualData, "users", ignore);
    }

    //others tests


}
