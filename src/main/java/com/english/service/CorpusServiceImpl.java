package com.english.service;

import com.english.Utils.FileUtils;
import com.english.baidutrans.TransUtil;
import com.english.javaBeans.Corpus;
import com.english.repository.CorpusDao;
import com.english.repository.CorpusDaoImpl;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public class CorpusServiceImpl implements CorpusService {
    public static final CorpusService CORPUS_SERVICE = new CorpusServiceImpl();
    private final CorpusDao corpusDao = CorpusDaoImpl.CORPUS_DAO;

    private CorpusServiceImpl() {
    }

    @Override
    public void save(Corpus corpus) {
        StringBuilder sql = new StringBuilder("insert into corpus values ");
        sql.append("(")
                .append(corpus.getEn()).append(",")
                .append(corpus.getEnText()).append(",")
                .append(corpus.getZhText())
                .append(")");
        corpusDao.insert(sql.toString());
    }

    @Override
    public void save(List<Corpus> dataList) {
        StringBuilder sql = new StringBuilder("insert into corpus values ");
        for (Corpus corpus : dataList) {
            sql.append("(")
                    .append(corpus.getEn()).append(",")
                    .append(corpus.getEnText()).append(",")
                    .append(corpus.getZhText())
                    .append("),");
        }
        sql.deleteCharAt(sql.length());
        corpusDao.insert(sql.toString());
    }

    @Override
    public void saveByFile(String filePath) {
        String fileSuffix = "txt";
        if (!filePath.endsWith(fileSuffix)) {
            return;
        }
        Properties properties = FileUtils.load(filePath);
        Set<String> enSet = properties.stringPropertyNames();
        List<Corpus> corpusList = new ArrayList<>();
        String regex = "([\\w\\s\\pP]+)([\u4e00-\u9fa5\\w\\pP]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        for (String en : enSet) {
            String text = properties.getProperty(en);
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                Corpus corpus = new Corpus(en, matcher.group(1), matcher.group(2));
                corpusList.add(corpus);
            }
        }
        save(corpusList);
    }

    @Override
    public Corpus translate(String sentence, String from, String to) {
        Corpus corpus = null;
        try {
            String transResult = TransUtil.getTransResult(sentence, from, to);
            if (StringUtils.hasText(transResult)) {
                corpus = new Corpus();
                Class<Corpus> aClass = Corpus.class;
                Field f1 = aClass.getDeclaredField(from + "Text");
                Field f2 = aClass.getDeclaredField(to + "Text");
                f1.setAccessible(true);
                f2.setAccessible(true);
                f1.set(corpus, sentence);
                f2.set(corpus, transResult);
            }
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
        return corpus;
    }

    @Override
    public List<Corpus> queryRandom(int amount) {
        return corpusDao.select("select * from corpus order by rand() limit " + amount);
    }
}
