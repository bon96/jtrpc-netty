package org.bonbom.communication;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 16.44
 * Thanks alex for the name!
 */


public class RemoteMethodCall implements RemoteObject {

    private String senderName;
    private String receiverName;
    private String className;
    private String classNameSimple;
    private String methodName;

    private List<byte[]> objects;
    private List<String> parameterTypes = new ArrayList<>();

    private boolean callBySimpleName = false;

    private long id;

    public RemoteMethodCall(String senderName, String receiverName, Class clazz, Method method, Object... objects) throws Exception {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.className = clazz.getName();
        this.classNameSimple = clazz.getSimpleName();
        this.methodName = method.getName();
        this.id = ThreadLocalRandom.current().nextLong();

        for (Class c : method.getParameterTypes()) {
            parameterTypes.add(c.getName());
        }

        this.objects = new ArrayList<>();

        if (objects.length > 0) {
            for (Object object : objects) {
                ByteBuf byteBuf = Unpooled.buffer();
                encode((Serializable) object, byteBuf);
                this.objects.add(byteBuf.array());
            }
        }
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameSimple() {
        return classNameSimple;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getObjects(ObjectDecoder decoder) throws Exception {
        if (objects.isEmpty()) {
            return null;
        }

        Object[] objects = new Object[this.objects.size()-1];
        for (int i = 0 ; i < this.objects.size(); i++) {
            objects[i] = decoder.decode(null, Unpooled.wrappedBuffer(this.objects.get(i)));
        }
        return objects;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public boolean isCallBySimpleName() {
        return callBySimpleName;
    }

    public void setCallBySimpleName(boolean callBySimpleName) {
        this.callBySimpleName = callBySimpleName;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() { //hashCode should match remoteMethod
        if (isCallBySimpleName()) {
            return classNameSimple.hashCode() + methodName.hashCode() + parameterTypes.hashCode();
        }
        return className.hashCode() + methodName.hashCode() + parameterTypes.hashCode();
    }

    @Override
    public String toString() {
        return "sender: " + senderName + ", receiver: " + receiverName +
                ", method: " + className + "::" + methodName + ", parameters: " + parameterTypes.toString();
    }
}
