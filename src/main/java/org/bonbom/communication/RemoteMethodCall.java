package org.bonbom.communication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    private Object[] objects;
    private List<String> parameterTypes = new ArrayList<>();
    private boolean callBySimpleName = false;

    public RemoteMethodCall(String senderName, String receiverName, Class clazz, Method method, Object... objects) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.className = clazz.getName();
        this.classNameSimple = clazz.getSimpleName();
        this.methodName = method.getName();
        this.objects = objects;

        for (Class c : method.getParameterTypes()) {
            parameterTypes.add(c.getName());
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

    public Object[] getObjects() {
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

    @Override //TODO is there a better way?
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
