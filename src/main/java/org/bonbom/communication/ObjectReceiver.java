package org.bonbom.communication;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thavam.util.concurrent.blockingMap.BlockingHashMap;

import java.util.concurrent.TimeUnit;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.46
 */

public class ObjectReceiver {

    public static long TIMEOUT = 10000;

    private static final Logger logger = LoggerFactory.getLogger(ObjectReceiver.class);

    private final BlockingHashMap<Long, Object> receives = new BlockingHashMap<>();

    public Object get(long id) {
        logger.debug("Retrieving object with id {}", id);
        try {
            Object object = receives.take(id, TIMEOUT, TimeUnit.MILLISECONDS);
            if (logger.isDebugEnabled() && object == null) {
                logger.debug("TIMEOUT or null object with id {} in map", id);
            }
            return object;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onReceive(RemoteAnswer remoteAnswer) {
        receives.put(remoteAnswer.getId(), remoteAnswer.getObject());
    }
}
