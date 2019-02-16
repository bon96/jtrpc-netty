package org.bonbom.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 16.44
 * Thanks alex for the name!
 */


public class RemoteMethodCall implements Serializable {

    private String senderName;
    private String receiverName;
    private String className;
    private String methodName;
    private Object[] objects;
    private List<String> parameterTypes = new ArrayList<>();

    public RemoteMethodCall(String senderName, String receiverName, String className, String methodName, Object... objects) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.className = className;
        this.methodName = methodName;
        this.objects = objects;

        for (Object object : objects) {
            if (object == null) {
                parameterTypes.add(null);        // try to match method without
                continue;                       // knowledge of parameter type
            }
            parameterTypes.add(object.getClass().getName());
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

    public String getMethodName() {
        return methodName;
    }

    public Object[] getObjects() {
        return objects;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public int hashCode() { //hashCode should match remoteMethod
        return className.hashCode() + methodName.hashCode() + parameterTypes.hashCode();
    }

    @Override
    public String toString() {
        return "sender: " + senderName + ", receiver: " + receiverName +
                ", method: " + className + "::" + methodName + ", parameters: " + parameterTypes.toString();
    }
}
