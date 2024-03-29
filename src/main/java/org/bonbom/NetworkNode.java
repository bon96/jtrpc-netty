package org.bonbom;

import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.logging.LogLevel;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.bonbom.communication.ObjectDecoder;
import org.bonbom.communication.ObjectReceiver;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethod;
import org.bonbom.communication.RemoteMethodCall;
import org.bonbom.communication.RemoteObject;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetworkNode {

    private static final Logger logger = LoggerFactory.getLogger(NetworkNode.class);
    private static final Objenesis objenesis = new ObjenesisStd();
    LogLevel logLevel;
    boolean running = false;
    private int threads = 10;

    private ObjectReceiver receiver;
    private ObjectDecoder decoder;

    private Map<Integer, RemoteMethod> registeredMethods = new HashMap<>();

    {
        this.decoder = new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader()));
        this.receiver = new ObjectReceiver(decoder);
    }

    public abstract String getName();

    abstract void stop();

    abstract void send(RemoteObject remoteObject);

    abstract Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException;

    public void onReceive(RemoteAnswer remoteAnswer) {
        logger.debug("Received RemoteAnswer for id {}: {}", remoteAnswer.getId(), remoteAnswer);

        if (!this.getName().equals(remoteAnswer.getReceiverName())) {
            send(remoteAnswer);
            return;
        }
        receiver.onReceive(remoteAnswer);
    }

    public ObjectReceiver getReceiver() {
        return receiver;
    }

    public void onReceive(RemoteMethodCall remoteMethodCall) {
        logger.debug("Received RemoteMethodCall for id {}: {}", remoteMethodCall.getId(), remoteMethodCall);

        if (!this.getName().equals(remoteMethodCall.getReceiverName())) {
            send(remoteMethodCall);
            return;
        }

        RemoteMethod remoteMethod = getMatch(remoteMethodCall);
        if (remoteMethod != null) {
            try {
             //   if (remoteMethod.getMethod().getReturnType().equals(void.class)) {
            //        remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects(decoder));
           //         return;
            //    }

                send(new RemoteAnswer(
                        remoteMethodCall.getSenderName(),
                        remoteMethodCall.getId(),
                        remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects(decoder))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        logger.error("No matches found for method " + remoteMethodCall.getClassName() + "::" + remoteMethodCall.getMethodName(), new IllegalArgumentException());
    }

    public RemoteMethod getMatch(RemoteMethodCall remoteMethodCall) {
        return registeredMethods.get(remoteMethodCall.hashCode());
    }

    public void registerMethods(Object instance, List<Method> methods) {
        if (logger.isDebugEnabled())
            logger.debug("Registering {} methods: {} ", instance.getClass().getName(), methods);

        for (Method method : methods) {
            RemoteMethod remoteMethod = new RemoteMethod(instance, method);
            registeredMethods.put(remoteMethod.hashCode(), remoteMethod);
            registeredMethods.put(remoteMethod.simpleHashCode(), remoteMethod);
        }
    }

    public void registerMethods(Object instance) {
        registerMethods(instance, Arrays.asList(instance.getClass().getMethods()));
    }

    public void registerMethods(Class clazz) throws Exception {
        registerMethods(clazz.newInstance());
    }

    public void registerMethods(Class interf, Object instance, List<Method> methods) {
        if (logger.isDebugEnabled())
            logger.debug("Registering methods from {} mapped to {}: {}", interf.getName(), instance.getClass().getName(), methods);

        for (Method method : methods) {
            RemoteMethod remoteMethod = new RemoteMethod(interf, instance, method);
            registeredMethods.put(remoteMethod.hashCode(), remoteMethod);
            registeredMethods.put(remoteMethod.simpleHashCode(), remoteMethod);
        }
    }

    public void registerMethods(Class interf, Object implInstance) {
        registerMethods(interf, implInstance, Arrays.asList(implInstance.getClass().getMethods()));
    }

    public void registerMethods(Class interf, Class impl) throws Exception {
        registerMethods(interf, impl.newInstance());
    }

    public Collection<RemoteMethod> getRegisteredMethods() {
        return registeredMethods.values();
    }

    public <T> T createProxy(String clientName, Class proxyClass) {
        return createProxy(clientName, proxyClass, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(String clientName, Class proxyClass, boolean ignorePath) {
        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallbackType(MethodInterceptor.class);

        Class clazz = enhancer.createClass();
        Enhancer.registerCallbacks(clazz, new Callback[]{(MethodInterceptor) (obj, method, args1, proxy) -> {
            if (method.getDeclaringClass() != Object.class) {
                RemoteMethodCall remoteMethodCall = new RemoteMethodCall(getName(), clientName, method.getDeclaringClass(), method, args1);
                remoteMethodCall.setCallBySimpleName(ignorePath);


                /*
                if (method.getReturnType() == void.class) {
                    send(remoteMethodCall);
                    return null;
                }
                 */
                return sendAndWait(remoteMethodCall);
            }
            return proxy.invokeSuper(obj, args1);
        }});
        Object instance = objenesis.newInstance(clazz);
        instance.hashCode(); // hacky af. Some shit requires any method call to fix a weird bug with JavaFX Platform::runLater
        return (T) instance;
    }


    //TODO adding support for remote objects
/*
    public <T> T remoteOf(Class clazz, Object... objects) {
        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setSuperclass(clazz);
        });
    }
 */

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public boolean isRunning() {
        return running;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
