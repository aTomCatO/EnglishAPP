package com.english.repository;

import com.english.Utils.FileUtils;
import com.english.Utils.InstanceUtils;
import com.english.service.DictionaryServiceImpl;

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
        Properties properties;
        String url;
        String user = null;
        String password = null;
        try {
            properties = FileUtils.load("D:\\JavaWorld\\Demo\\EnglishApp\\src\\main\\resources\\config.properties");
            if (properties != null) {
                String driverClass = properties.getProperty("driverClass");
                url = properties.getProperty("url");
                user = properties.getProperty("user");
                password = properties.getProperty("password");
                Class.forName(driverClass);
                connection = DriverManager.getConnection(url, user, password);
                statement = connection.createStatement();
            }
        } catch (SQLException e) {
            String message = e.getMessage();
            InstanceUtils.LOGGER.error(message);
            String linkFailure = "Communications link failure";
            String unknownData = "Unknown database 'EnglishApp'";
            if (message.contains(linkFailure)) {
                InstanceUtils.LOGGER.info("原因: MySQL服务已关闭");
                System.exit(0);
            } else if (message.contains(unknownData)) {
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password);
                    statement = connection.createStatement();
                    statement.execute("create database EnglishApp");
                    statement.execute("use EnglishApp");
                    statement.execute("create table dictionary(en varchar(16) primary key comment '单词', zh varchar(66) comment '中文翻译')");
                    statement.execute("create table corpus(en varchar(16) comment '单词',enText text comment '例句',zhText text comment '中文翻译')");
                    DictionaryServiceImpl.DICTIONARY_SERVICE.saveByFile("dataFile/dictionary.txt");
                } catch (SQLException sqlException) {
                    InstanceUtils.LOGGER.error(sqlException.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            InstanceUtils.LOGGER.error(e.getMessage());
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
