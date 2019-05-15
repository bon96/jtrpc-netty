package org.bonbom.communication;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolver;

import java.io.ObjectInputStream;

public class ObjectDecoder extends LengthFieldBasedFrameDecoder {
    private final ClassResolver classResolver;

    public ObjectDecoder(ClassResolver classResolver) {
        this(1048576, classResolver);
    }

    public ObjectDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, 0, 4, 0, 4);
        this.classResolver = classResolver;
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf)super.decode(ctx, in);
        if (frame == null) {
            return null;
        } else {
            ObjectInputStream is = new CompactObjectInputStream(new ByteBufInputStream(frame), this.classResolver);
            Object result = is.readObject();
            is.close();
            return result;
        }
    }

    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
