package com.ua.sutty.jdbc;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.repository.JdbcRoleDao;
import com.ua.sutty.jdbc.repository.JdbcUserDao;
import com.ua.sutty.jdbc.utils.CreateTable;

import java.sql.Date;

public class Main {

    public static void main(String[] args) {

        CreateTable createTable = new CreateTable();
        createTable.createTableRoles();
        createTable.createTableUsers();


        JdbcUserDao jdbcUserDao = JdbcUserDao.instance();
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.instance();
        Date birthday = new Date(new java.util.Date().getTime());
        Role role = Role.builder()
//            .id(1L)
            .name("USER")
            .build();
//        jdbcRoleDao.create(role);
//        System.out.println(jdbcRoleDao.findByName("USER"));
//
        User user = User.builder()
            .login("Login2")
            .password("Password2")
            .email("Email2")
            .firstName("First_name2")
            .lastName("Last_name2")
            .birthday(birthday)
            .roleId(1L)
            .build();
//        jdbcUserDao.create(user);
        Role roleForDelete = new Role();
        roleForDelete.setId(1L);
//        jdbcRoleDao.remove(roleForDelete);
        System.out.println(jdbcUserDao.findAll());
        System.out.println(jdbcRoleDao.findByName("USER"));
//        jdbcRoleDao.remove(role);

    }

}
