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

    public Object get(long id) throws InterruptedException {
        return receives.take(id, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void onReceive(RemoteAnswer remoteAnswer) {
        logger.debug("Received RemoteAnswer: {}", remoteAnswer);
        receives.put(remoteAnswer.getId(), remoteAnswer.getObject());
    }
}
