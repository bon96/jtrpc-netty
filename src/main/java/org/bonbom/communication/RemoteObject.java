package org.bonbom.communication;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Tommi
 * Date: 5.5.2019
 * Time: 14.21
 */

public interface RemoteObject extends Serializable {
    byte[] LENGTH_PLACEHOLDER = new byte[4];

    String getReceiverName();

    default void encode(Serializable object, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(LENGTH_PLACEHOLDER);
        ObjectOutputStream oout = new CompactObjectOutputStream(bout);
        oout.writeObject(object);
        oout.flush();
        oout.close();
        int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
    }
}
