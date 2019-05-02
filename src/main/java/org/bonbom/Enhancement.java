package org.bonbom;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.bonbom.communication.RemoteMethodCall;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Tommi
 * Date: 17.1.2019
 * Time: 16.10
 */

public class Enhancement {

    private static Objenesis objenesis = new ObjenesisStd();

    public static <T> T createProxy(NetworkNode networkNode, Class proxyClass) {
        return createProxy(networkNode, proxyClass, false);
    }

    public static <T> T createProxy(NetworkNode networkNode, Class proxyClass, boolean ignorePath) {
        return createProxy(networkNode, "server", proxyClass, ignorePath);
    }

    public static <T> T createProxy(NetworkNode networkNode, String clientName, Class proxyClass) {
        return createProxy(networkNode, clientName, proxyClass, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(NetworkNode networkNode, String clientName, Class proxyClass, boolean ignorePath) {
        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallbackType(MethodInterceptor.class);

        Class clazz = enhancer.createClass();
        Enhancer.registerCallbacks(clazz, new Callback[]{(MethodInterceptor) (obj, method, args1, proxy) -> {
            if (method.getDeclaringClass() != Object.class) {
                RemoteMethodCall remoteMethodCall = new RemoteMethodCall(networkNode.getName(), clientName, method.getDeclaringClass(), method, args1);
                remoteMethodCall.setCallBySimpleName(ignorePath);
                if (method.getReturnType() == void.class) {
                    networkNode.send(remoteMethodCall);
                    return null;
                }
                return networkNode.sendAndWait(remoteMethodCall);
            }
            return proxy.invokeSuper(obj, args1);
        }});
        Object instance = objenesis.newInstance(clazz);
        instance.hashCode(); // hacky af. Some shit requires any method call to fix a weird bug with JavaFX Platform::runLater
        return (T) instance;
    }
}
