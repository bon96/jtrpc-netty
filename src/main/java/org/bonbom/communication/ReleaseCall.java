package org.bonbom.communication;

/**
 * Tommi
 * Date: 12.5.2019
 * Time: 1.01
 */

public class ReleaseCall implements RemoteObject {

    private String receiver;
    private int id;

    public ReleaseCall(String receiver, int id) {
        this.receiver = receiver;
        this.id = id;
    }

    @Override
    public String getReceiverName() {
        return receiver;
    }

    public int getId() {
        return id;
    }
}
