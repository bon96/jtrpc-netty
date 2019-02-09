package org.bonbom.communication;

import java.util.HashMap;
import java.util.Map;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.46
 */

public class ObjectReceiver {

    private Map<Long, FutureReceive> receives = new HashMap<>();

    public Object get(long id) throws InterruptedException {
        if (!receives.containsKey(id)) {
            receives.put(id, new FutureReceive());
        }
        Object received = receives.get(id).get();
        receives.remove(id);
        return received;
    }

    public void onReceive(RemoteAnswer remoteAnswer) {
        if (!receives.containsKey(remoteAnswer.getId())) {
            receives.put(remoteAnswer.getId(), new FutureReceive());
        }
        receives.get(remoteAnswer.getId()).receive(remoteAnswer.getObject());
    }
}
