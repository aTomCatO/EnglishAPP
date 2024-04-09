package com.english.repository;

import com.english.util.FileUtils;
import com.english.service.DictionaryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDao.class);
    protected static Connection connection;
    protected static Statement statement;

    static {
        Properties properties;
        String url;
        String user = null;
        String password = null;
        try {
            properties = FileUtils.load("server.properties");
            String driverClass = properties.getProperty("driverClass");
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            String message = e.getMessage();
            LOGGER.info("【ERROR】: " + message);
            String linkFailure = "Communications link failure";
            String unknownData = "Unknown database 'EnglishApp'";
            if (message.contains(linkFailure)) {
                LOGGER.info("\n原因: 数据库服务已关闭,无法得到连接 \n解决: 开启此服务");
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
                } catch (SQLException ex) {
                    LOGGER.info("【ERROR】: " + ex.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("【ERROR】: " + e.getMessage());
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
