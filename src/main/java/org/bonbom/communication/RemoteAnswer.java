package org.bonbom.communication;

import java.io.Serializable;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.24
 */

public class RemoteAnswer implements Serializable {

    private String receiverName;
    private long id;
    private Object object;

    public RemoteAnswer(String receiverName, long id, Object object) {
        this.receiverName = receiverName;
        this.id = id;
        this.object = object;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public long getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return receiverName + ", " + id + ", " + object.toString();
    }
}