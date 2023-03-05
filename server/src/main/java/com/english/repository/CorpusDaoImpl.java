package com.english.repository;

import com.english.entity.Corpus;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 */
public class CorpusDaoImpl extends AbstractDao<Corpus> implements CorpusDao {
    public static final CorpusDao CORPUS_DAO = new CorpusDaoImpl();

    private CorpusDaoImpl() {

    }

    @Override
    public List<Corpus> select(String sql) {
        List<Corpus> corpusList = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (columnCount == 2) {
                while (resultSet.next()) {
                    Corpus corpus = new Corpus(resultSet.getString(1), resultSet.getString(2));
                    corpusList.add(corpus);
                }
            } else if (columnCount == 3) {
                while (resultSet.next()) {
                    Corpus corpus = new Corpus(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                    corpusList.add(corpus);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return corpusList;
    }
}
