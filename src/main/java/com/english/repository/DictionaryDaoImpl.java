package com.english.repository;

import com.english.entity.Dictionary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 */

public class DictionaryDaoImpl extends AbstractDao<Dictionary> implements DictionaryDao {
    public static final DictionaryDao DICTIONARY_DAO = new DictionaryDaoImpl();

    private DictionaryDaoImpl() {
    }

    @Override
    public List<Dictionary> select(String sql) {
        List<Dictionary> dictionaryList = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Dictionary dictionary = new Dictionary(resultSet.getString(1), resultSet.getString(2));
                dictionaryList.add(dictionary);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dictionaryList;
    }
}
