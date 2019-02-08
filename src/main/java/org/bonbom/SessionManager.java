package org.bonbom;

import com.github.thorbenkuck.netcom2.network.shared.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 23.55
 */

public class SessionManager {

    private Map<String, Session> sessionMap = new HashMap<>();

    public void register(String name, Session session) {
        sessionMap.put(name, session);
    }

    public void unRegister(String name) {
        sessionMap.remove(name);
    }

    public Session get(String name) {
        return sessionMap.get(name);
    }

    public boolean contains(String name) {
        return sessionMap.containsKey(name);
    }

    public boolean contains(Session session) {
        return sessionMap.containsValue(session);
    }

    public List<String> getSessionNames() {
        return new ArrayList<>(sessionMap.keySet());
    }
}
