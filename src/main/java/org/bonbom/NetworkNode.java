package org.bonbom;

import io.netty.channel.ChannelHandlerContext;
import org.bonbom.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkNode {

    private static final Logger logger = LoggerFactory.getLogger(FutureReceive.class);

    private List<RemoteMethod> remoteMethods = new ArrayList<>();
    private ObjectReceiver receiver = new ObjectReceiver();

    public abstract String getName();

    abstract void send(RemoteMethodCall remoteMethodCall);
    abstract void send(RemoteAnswer remoteAnswer);
    abstract Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException;


    public void onReceive(RemoteAnswer remoteAnswer, ChannelHandlerContext ctx) {
        logger.debug("Received RemoteAnswer: {}", remoteAnswer);

        if(!this.getName().equals(remoteAnswer.getReceiverName())) {
            send(remoteAnswer);
            return;
        }
        receiver.onReceive(remoteAnswer);
    }

    public ObjectReceiver getReceiver() {
        return receiver;
    }

    public void onReceive(RemoteMethodCall remoteMethodCall, ChannelHandlerContext ctx) {
        logger.debug("Received RemoteMethodCall: {}", remoteMethodCall);

        if (!this.getName().equals(remoteMethodCall.getReceiverName())) {
            send(remoteMethodCall);
            return;
        }

        RemoteMethod remoteMethod = getMatch(remoteMethodCall);
        if (remoteMethod != null) {
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
        logger.error("No matches found for method " + remoteMethodCall.getClassName() + "::" + remoteMethodCall.getMethodName(), new IllegalArgumentException());
    }

    public RemoteMethod getMatch(RemoteMethodCall remoteMethodCall) {
        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.match(remoteMethodCall)) {
                return remoteMethod;
            }
        }
        return null;
    }

    public void registerMethods(Object instance, List<Method> methods) {
        if (logger.isDebugEnabled()) logger.debug("Registering {} methods: {} ", instance.getClass().getName(), methods);

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
        if (logger.isDebugEnabled()) logger.debug("Registering methods from {} mapped to {}: {}", interf.getName(), instance.getClass().getName(), methods);

        for (Method method : methods) {
            remoteMethods.add(new RemoteMethod(interf, instance, method));
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
