package com.ua.sutty.jdbc;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.repository.JdbcRoleDao;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JdbcRoleTest extends DBUnitConfig{

    private JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();

    public JdbcRoleTest(String name) {
        super(name);
    }

//    private PersonService service = new PersonService();
//    private EntityManager em = Persistence.createEntityManagerFactory("DBUnitEx").createEntityManager();

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
    public void testSave() throws Exception {
        Role role = new Role();
        role.setName("USER");
        jdbcRoleDao.create(role);

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
            Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("com/devcolibri/entity/person/person-data-save.xml"));

        IDataSet actualData = tester.getConnection().createDataSet();

        String[] ignore = {"id"};
        Assertion.assertEqualsIgnoreCols(expectedData, actualData, "person", ignore);
    }

    //others tests

}