package com.english.service;

import com.english.baidutrans.TransUtil;
import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.repository.CorpusDao;
import com.english.repository.CorpusDaoImpl;
import com.english.repository.DictionaryDao;
import com.english.repository.DictionaryDaoImpl;
import com.english.util.FileUtil;
import com.english.util.InstanceUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public class DictionaryServiceImpl implements DictionaryService {
    public static final DictionaryService DICTIONARY_SERVICE = new DictionaryServiceImpl();
    private final DictionaryDao dictionaryDao = DictionaryDaoImpl.DICTIONARY_DAO;
    private final CorpusDao corpusDao = CorpusDaoImpl.CORPUS_DAO;

    private DictionaryServiceImpl() {
    }

    @Override
    public void save(Dictionary dictionary) {
        StringBuilder sql = new StringBuilder("insert into dictionary values ");
        sql.append("(")
                .append(dictionary.getEn()).append(",")
                .append(dictionary.getZh())
                .append(")");
        dictionaryDao.insert(sql.toString());
    }

    @Override
    public void save(List<Dictionary> dataList) {
        StringBuilder sql = new StringBuilder("insert into dictionary values ");
        for (Dictionary dictionary : dataList) {
            sql.append("(")
                    .append(dictionary.getEn()).append(",")
                    .append(dictionary.getZh())
                    .append("),");
        }
        sql.deleteCharAt(sql.length());
        dictionaryDao.insert(sql.toString());
    }

    @Override
    public void saveByFile(String filePath) {
        String fileSuffix = "txt";
        Properties properties;
        if (filePath.endsWith(fileSuffix) && (properties = FileUtil.load(filePath)) != null) {
            //首字母集合
            Set<String> initialSet = properties.stringPropertyNames();
            LinkedBlockingDeque<List<Dictionary>> dictionaryDeque = new LinkedBlockingDeque<>(8);
            AtomicBoolean end = new AtomicBoolean(true);
            //生产者线程,负责将单词文本里的单词和中文翻译进行抽取
            THREAD_POOL.execute(() -> {
                String regex = "([a-zA-Z]+)\\s([a-zA-Z&.]+\\.[\u4e00-\u9fa5\\pP]+(\\s[a-zA-Z&.]+\\.[\u4e00-\u9fa5\\pP]+)*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher;
                Iterator<String> iterator = initialSet.stream().iterator();
                while (iterator.hasNext()) {
                    List<Dictionary> dictionaryList = new ArrayList<>();
                    String wordText = iterator.next();
                    matcher = pattern.matcher(wordText);
                    while (matcher.find()) {
                        String en = matcher.group(1);
                        String zh = matcher.group(2);
                        Dictionary dictionary = new Dictionary(en, zh);
                        dictionaryList.add(dictionary);
                    }
                    dictionaryDeque.add(dictionaryList);
                }
                end.set(false);
            });
            //消费者线程,负责调用Dao将数据集保存到数据库
            THREAD_POOL.execute(() -> {
                        while (end.get() || !dictionaryDeque.isEmpty()) {
                            try {
                                //队列没有元素时将会阻塞
                                List<Dictionary> dictionaryList = dictionaryDeque.takeLast();
                                save(dictionaryList);
                            } catch (Exception e) {
                                InstanceUtil.LOGGER.error(e.getMessage());
                            }
                        }
                    }
            );
        }
    }

    @Override
    public List<Dictionary> translate(String word, String from, String to) {
        //"select * from dictionary d left join corpus c on d.en=c.en where d." + from + " like '%" + word + "%'"
        List<Dictionary> dictionaryList =
                dictionaryDao.select("select * from dictionary where " + from + " like '%" + word + "%'");
        if (!dictionaryList.isEmpty()) {
            addCorpus(dictionaryList);
        } else {
            try {
                Dictionary dictionary = new Dictionary();
                Class<? extends Dictionary> aClass = dictionary.getClass();
                Field f1 = aClass.getDeclaredField(from);
                Field f2 = aClass.getDeclaredField(to);
                f1.setAccessible(true);
                f2.setAccessible(true);
                f1.set(dictionary, word);
                f2.set(dictionary, TransUtil.translate(word, from, to));
                dictionaryList.add(dictionary);
            } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return dictionaryList;
    }

    @Override
    public List<Dictionary> queryRandom(int amount) {
        return dictionaryDao.select("select * from dictionary order by rand() limit " + amount);
    }

    @Override
    public List<Dictionary> queryRandomAddCorpus(int amount) {
        List<Dictionary> dictionaryList = queryRandom(amount);
        addCorpus(dictionaryList);
        return dictionaryList;
    }

    public void addCorpus(List<Dictionary> dictionaryList) {
        StringBuilder sql = new StringBuilder("select enText,zhText from corpus where ");
        for (Dictionary dictionary : dictionaryList) {
            sql.append("enText like '%").append(dictionary.getEn()).append("%' or ");
        }
        //删除最后的 or
        sql.delete(sql.length() - 3, sql.length());
        List<Corpus> corpusList = corpusDao.select(sql.toString());
        for (Dictionary dictionary : dictionaryList) {
            dictionary.setCorpusList(new ArrayList<>());
            for (Corpus corpus : corpusList) {
                if (corpus.getEnText().contains(dictionary.getEn())) {
                    dictionary.addCorpus(corpus);
                }
            }
        }
    }
}

