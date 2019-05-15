package org.bonbom.communication;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.24
 */

public class RemoteAnswer implements RemoteObject {

    private final Logger logger = LoggerFactory.getLogger(RemoteAnswer.class);

    private String receiverName;
    private long id;
    private byte[] bytes;
    private String objectString;

    public RemoteAnswer(String receiverName, long id, Object object) throws Exception {
        this.receiverName = receiverName;
        this.id = id;

        ByteBuf byteBuf = Unpooled.buffer();
        encode((Serializable) object, byteBuf);
        bytes = byteBuf.array();

        if (logger.isDebugEnabled() && object != null) {
            objectString = object.toString();
        }
    }

    public String getReceiverName() {
        return receiverName;
    }

    public long getId() {
        return id;
    }

    public Object getObject(ObjectDecoder decoder) throws Exception {
        return decoder.decode(null, Unpooled.wrappedBuffer(bytes));
    }

    @Override
    public String toString() {
        return receiverName + ", " + id + ", " + objectString;
    }
}
