package com.english.service;

import com.english.entity.Dictionary;
import com.english.netty.NettyService;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author XYC
 */
public class DictionaryServiceImpl extends NettyService implements DictionaryService {
    public static final DictionaryService DICTIONARY_SERVICE = new DictionaryServiceImpl();

    private DictionaryServiceImpl() {
    }

    @Override
    public List<Dictionary> translate(String word, String from, String to) {
        MESSAGE_MANAGER
                .setMessage("entity", "dictionary")
                .setMessage("method", "translate")
                .setMessage("args", word + "&" + from + "&" + to);
        sendMessage();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return messageContentHandler.getMsgContent(new TypeReference<List<Dictionary>>() {
        });
    }

    @Override
    public List<Dictionary> queryRandom(int amount) {
        MESSAGE_MANAGER
                .setMessage("entity", "dictionary")
                .setMessage("method", "queryRandom")
                .setMessage("args", amount);
        sendMessage();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return messageContentHandler.getMsgContent(new TypeReference<List<Dictionary>>() {
        });
    }

    @Override
    public List<Dictionary> queryRandomAddCorpus(int amount) {
        MESSAGE_MANAGER
                .setMessage("entity", "dictionary")
                .setMessage("method", "queryRandomAddCorpus")
                .setMessage("args", amount);
        sendMessage();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return messageContentHandler.getMsgContent(new TypeReference<List<Dictionary>>() {
        });
    }
}

