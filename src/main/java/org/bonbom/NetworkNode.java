package org.bonbom;

import org.bonbom.communication.ObjectReceiver;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethod;
import org.bonbom.communication.RemoteMethodCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkNode {

    private List<RemoteMethod> remoteMethods = new ArrayList<>();
    private ObjectReceiver receiver = new ObjectReceiver();

    abstract String getName();
    abstract void send(RemoteMethodCall remoteMethodCall);
    abstract void send(RemoteAnswer remoteAnswer);
    abstract Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException;


    public void onReceive(RemoteAnswer remoteAnswer) {
        if(!this.getName().equals(remoteAnswer.getReceiverName())) {
            send(remoteAnswer);
            return;
        }
        receiver.onReceive(remoteAnswer);
    }

    public ObjectReceiver getReceiver() {
        return receiver;
    }

    public void onReceive(RemoteMethodCall remoteMethodCall) {
        if (!this.getName().equals(remoteMethodCall.getReceiverName())) {
            send(remoteMethodCall);
            return;
        }

        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.hashCode() == remoteMethodCall.hashCode()) {
                try {
                    if (remoteMethod.getMethod().getReturnType().equals(void.class)) {
                        remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects());
                        return;
                    }
                    RemoteAnswer answer = new RemoteAnswer(
                            remoteMethodCall.getSenderName(),
                            remoteMethodCall.hashCode(),
                            remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects()));
                    send(answer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        throw new IllegalArgumentException("Method " + remoteMethodCall.getClassName() + "::" + remoteMethodCall.getMethodName()
                + " is not registered!");
    }

    public void registerMethods(Object instance, List<Method> methods) {
        for (Method method : methods) {
            remoteMethods.add(new RemoteMethod(instance, method));
        }
    }

    public void registerMethods(Object instance) {
        registerMethods(instance, Arrays.asList(instance.getClass().getMethods()));
    }

    public void registerMethods(Class clazz) throws Exception {
       registerMethods(clazz.newInstance());
    }

    public void registerMethods(Class interf, Object instance, List<Method> methods) {
        for (Method method : methods) {
            remoteMethods.add(new RemoteMethod(interf.getName(), instance, method));
        }
    }

    public void registerMethods(Class interf, Object implInstance) {
        registerMethods(interf, implInstance, Arrays.asList(implInstance.getClass().getMethods()));
    }

    public void registerMethods(Class interf, Class impl) throws Exception {
        registerMethods(interf, impl.newInstance());
    }

    public RemoteMethod getRegisteredMethod(String className, String methodName) {
        for (RemoteMethod method : remoteMethods) {
            if (method.getClassName().equals(className) && method.getMethodName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public List<RemoteMethod> getRegisteredMethods() {
        return remoteMethods;
    }
}
