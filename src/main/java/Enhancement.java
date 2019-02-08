import communication.RemoteMethodCall;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Tommi
 * Date: 17.1.2019
 * Time: 16.10
 */

public class Enhancement {


    public static <T> T createProxy(Client client, Class proxyClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            if (method.getDeclaringClass() != Object.class) {

                RemoteMethodCall remoteMethodCall = new RemoteMethodCall(client.getName(), method.getDeclaringClass().getName(), method.getName(), args1);
                if (method.getReturnType() == void.class) {
                    client.send(remoteMethodCall);
                    return null;
                } else {
                    return client.sendAndWait(remoteMethodCall);
                }
            } else {
                return proxy.invokeSuper(obj, args1);
            }
        });
        return (T) enhancer.create();
    }

    public static <T> T createProxy(Server server, String clientName, Class proxyClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            if (method.getDeclaringClass() != Object.class) {

                RemoteMethodCall remoteMethodCall = new RemoteMethodCall(server.getName(), method.getDeclaringClass().getName(), method.getName(), args1);
                if (method.getReturnType() == void.class) {
                    server.send(remoteMethodCall, clientName);
                    return null;
                } else {
                    return server.sendAndWait(remoteMethodCall, clientName);
                }
            } else {
                return proxy.invokeSuper(obj, args1);
            }
        });
        return (T) enhancer.create();
    }
}
