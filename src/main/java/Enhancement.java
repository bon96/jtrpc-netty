import communication.RemoteMethodCall;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Tommi
 * Date: 17.1.2019
 * Time: 16.10
 */

public class Enhancement {


    public static <T> T createProxy(NetworkNode networkNode, Class proxyClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            if (method.getDeclaringClass() != Object.class) {
                RemoteMethodCall remoteMethodCall = new RemoteMethodCall(method.getDeclaringClass().getName(), method.getName(), args1);
                if (method.getReturnType() == void.class) {
                    networkNode.send(remoteMethodCall);
                    return null;
                } else {
                    return networkNode.sendAndWait(remoteMethodCall);
                }
            } else {
                return proxy.invokeSuper(obj, args1);
            }
        });
        return (T) enhancer.create();
    }
}
