package com.english.service;

import com.english.entity.Corpus;
import com.english.netty.NettyService;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author XYC
 */
public class CorpusServiceImpl extends NettyService implements CorpusService {
    public static final CorpusService CORPUS_SERVICE = new CorpusServiceImpl();

    private CorpusServiceImpl() {
    }

    @Override
    public Corpus translate(String sentence, String from, String to) {
        MESSAGE_MANAGER
                .setMessage("entity", "corpus")
                .setMessage("method", "translate")
                .setMessage("args", from + "&" + to);
        sendMessage();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return messageContentHandler.getMsgContent(new TypeReference<Corpus>() {
        });
    }

    @Override
    public List<Corpus> queryRandom(int amount) {
        MESSAGE_MANAGER
                .setMessage("entity", "corpus")
                .setMessage("method", "queryRandom")
                .setMessage("args", amount);
        sendMessage();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return messageContentHandler.getMsgContent(new TypeReference<List<Corpus>>() {
        });
    }
}
