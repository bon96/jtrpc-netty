package org.bonbom.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.30
 */

//TODO is there a better way?
public class FutureReceive {

    private static final Logger logger = LoggerFactory.getLogger(FutureReceive.class);

    public static long timeout = 10000;

    private Object receivedObject = null;
    private boolean  received = false;
    private final CountDownLatch latch = new CountDownLatch(1);

    Object get() throws InterruptedException {
        if (!received) {
            logger.debug("Waiting for object");
            synchronized (latch) {
                latch.await(timeout, TimeUnit.MILLISECONDS);
            }
        }
        return receivedObject;
    }

    void receive(Object object) {
        logger.debug("Received {}", object);
        this.receivedObject = object;
        this.received = true;
        latch.countDown();
    }
}
