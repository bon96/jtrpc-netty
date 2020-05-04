package org.bonbom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 23.55
 */


public class SessionManager<T> {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private Map<String, T> sessionMap = new HashMap<>();

    public void register(String name, T t) {
        logger.debug("Registering client {}", name);
        sessionMap.put(name, t);
    }

    public void unRegister(String name) {
        logger.debug("Unregistering client {}", name);
        sessionMap.remove(name);
    }

    public void unRegister(T t) {
        unRegister(get(t));
    }

    public T get(String name) {
        return sessionMap.get(name);
    }

    public String get(T t) {
        for (Map.Entry entry : sessionMap.entrySet()) {
            if (entry.getValue().equals(t)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }

    public boolean contains(String name) {
        return sessionMap.containsKey(name);
    }

    public boolean contains(T t) {
        return sessionMap.containsValue(t);
    }

    public List<String> getSessionNames() {
        return new ArrayList<>(sessionMap.keySet());
    }
}
