package communication;

import java.io.Serializable;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.24
 */

public class RemoteAnswer implements Serializable {

    private String getReceiverName;
    private long id;
    private Object object;

    public RemoteAnswer(String getReceiverName, long id, Object object) {
        this.getReceiverName = getReceiverName;
        this.id = id;
        this.object = object;
    }

    public RemoteAnswer(long id, Object object) {
        this.getReceiverName = null;
        this.id = id;
        this.object = object;
    }

    public String getReceiverName() {
        return getReceiverName;
    }

    public long getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }
}
