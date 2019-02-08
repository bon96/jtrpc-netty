package org.bonbom.communication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 12.56
 */

public class RemoteMethod {

    private String className;
    private String methodName;
    private Object classInstance;
    private Method method;
    private List<String> parameterTypes = new ArrayList<>();

    public RemoteMethod(Object classInstance, Method method) {
        this.className = method.getDeclaringClass().getName();
        this.methodName = method.getName();
        this.classInstance = classInstance;
        this.method = method;
        for (Class clazz : method.getParameterTypes()) {
            parameterTypes.add(clazz.getName());
        }
    }

    public RemoteMethod(String interfaceName, Object implClassInstance, Method method) {
        this(implClassInstance, method);
        this.className = interfaceName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public Method getMethod() {
        return method;
    }

    public Object getClassInstance() {
        return classInstance;
    }

    @Override
    public int hashCode() { //hashCode should match RemoteMethodCall
        return getClassName().hashCode() + getMethodName().hashCode() + parameterTypes.hashCode();
    }
}
