package communication;

import java.io.Serializable;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.24
 */

public class RpcAnswer implements Serializable {

    private long id;
    private Object object;

    public RpcAnswer(long id, Object object) {
        this.id = id;
        this.object = object;
    }

    public long getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }
}
