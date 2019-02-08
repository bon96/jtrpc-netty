import communication.RemoteMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkNode {

    private List<RemoteMethod> remoteMethods = new ArrayList<>();

    abstract String getName();

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
