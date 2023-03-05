package com.english.netty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 * 请求消息管理器
 */
public class MessageManager {
    private final Map<String, Object> messageMap = new HashMap<>();

    public Map<String, Object> getMessageMap() {
        return messageMap;
    }

    public boolean containsMessage(String name) {
        return messageMap.containsKey(name);
    }

    public Object getMessage(String name) {
        return messageMap.get(name);
    }

    public MessageManager setMessage(String name, Object value) {
        messageMap.put(name, value);
        return this;
    }

    public MessageManager removeMessage(String name) {
        messageMap.remove(name);
        return this;
    }
}
