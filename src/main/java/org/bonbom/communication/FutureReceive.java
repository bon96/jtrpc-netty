package org.bonbom.communication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.30
 */

class FutureReceive {

    public static long timeout = 10000;

    private Object receivedObject = null;
    private boolean  received = false;
    private final CountDownLatch latch = new CountDownLatch(1);

    Object get() throws InterruptedException {
        if (!received) {
            synchronized (latch) {
                latch.await(timeout, TimeUnit.MILLISECONDS);
            }
        }
        return receivedObject;
    }

    void receive(Object object) {
        this.receivedObject = object;
        this.received = true;
        latch.countDown();
    }
}
