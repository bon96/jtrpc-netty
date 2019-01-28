package communication;

import java.util.concurrent.CountDownLatch;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.30
 */

class FutureReceive {

    private Object receivedObject = null;
    private boolean  received = false;
    private CountDownLatch latch = new CountDownLatch(1);

    Object get() throws InterruptedException {
        if (!received) {
            synchronized (latch) {
                latch.await();
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
