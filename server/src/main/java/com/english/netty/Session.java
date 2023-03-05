package com.english.netty;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 */
@Data
@AllArgsConstructor
public class Session {
    private final Map<String, Object> map = new HashMap<>();
    private Channel channel;

    public void setAttribute(String name, Object value) {
        map.put(name, value);
    }

    public Object getAttribute(String name) {
        return map.get(name);
    }
}
