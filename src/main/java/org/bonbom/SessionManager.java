package org.bonbom;

import com.github.thorbenkuck.netcom2.network.shared.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 23.55
 */

@Slf4j
public class SessionManager {

    private Map<String, Session> sessionMap = new HashMap<>();

    public void register(String name, Session session) {
        log.debug("Registering client {}", name);
        sessionMap.put(name, session);
    }

    public void unRegister(String name) {
        log.debug("Unregistering client {}", name);
        sessionMap.remove(name);
    }

    public void unRegister(Session session) {
        for (Map.Entry entry : sessionMap.entrySet()) {
            if (entry.getValue().equals(session)) {
                unRegister(entry.getKey().toString());
            }
        }
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
