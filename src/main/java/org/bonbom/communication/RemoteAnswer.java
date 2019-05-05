package org.bonbom.communication;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.24
 */

public class RemoteAnswer implements RemoteObject {

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
        return receiverName + ", " + id + ", " + (object == null ? null : object.toString());
    }
}
