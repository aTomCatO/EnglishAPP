package com.english.repository;

import com.english.service.DictionaryServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author XYC
 * EnglisAPP.DAO
 */

public abstract class AbstractDao<T> implements BaseDao<T> {
    protected static Connection connection;
    protected static Statement statement;

    static {
        Properties properties = null;
        String url = null;
        String user = null;
        String password = null;
        try {
            InputStream inputStream = AbstractDao.class.getClassLoader().getResourceAsStream("EnglishAppData.properties");
            System.out.println(inputStream);
            properties = new Properties();
            properties.load(inputStream);
            String driverClass = properties.getProperty("driverClass");
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            String message = e.getMessage();
            System.out.println("[ERROR]: " + message);
            String linkFailure = "Communications link failure";
            String unknownData = "Unknown database 'EnglishApp'";
            if (message.contains(linkFailure)) {
                System.out.println("\n原因: 数据库服务已关闭,无法得到连接 \n解决: 开启此服务");
                System.exit(0);
            } else if (message.contains(unknownData)) {
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password);
                    statement = connection.createStatement();
                    statement.execute("create database EnglishApp");
                    statement.execute("use EnglishApp");
                    statement.execute("create table dictionary(en varchar(16) primary key comment '单词', zh varchar(66) comment '中文翻译')");
                    statement.execute("create table corpus(en varchar(16) comment '单词',enText text comment '例句',zhText text comment '中文翻译')");
                    DictionaryServiceImpl.DICTIONARY_SERVICE.saveByFile("file/dictionary.txt");
                } catch (SQLException ex) {
                    System.out.println("[ERROR]: " + ex.getMessage());
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }
    @Override
    public void insert(String sql) {
        try {
            statement.addBatch(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
