package com.english.netty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XYC
 */
public class SessionManager {
    private final Map<String, Session> sessionMap = new HashMap<>();

    public void setSession(String sessionId, Session session) {
        sessionMap.put(sessionId, session);
    }

    public Session getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

}
