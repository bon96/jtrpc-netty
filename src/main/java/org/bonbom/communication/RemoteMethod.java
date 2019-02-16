package org.bonbom.communication;

import jdk.nashorn.internal.runtime.regexp.joni.constants.AsmConstants;

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
    private String classNameSimple;
    private String methodName;
    private Object classInstance;
    private Method method;
    private List<String> parameterTypes = new ArrayList<>();

    public RemoteMethod(Object classInstance, Method method) {
        this.className = method.getDeclaringClass().getName();
        this.classNameSimple = method.getDeclaringClass().getSimpleName();
        this.methodName = method.getName();
        this.classInstance = classInstance;
        this.method = method;

        for (Class clazz : method.getParameterTypes()) {
            parameterTypes.add(clazz.getName());
        }
    }

    public RemoteMethod(Class interfaceClass, Object implClassInstance, Method method) {
        this(implClassInstance, method);
        this.className = interfaceClass.getName();
        this.classNameSimple = interfaceClass.getSimpleName();
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

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public Method getMethod() {
        return method;
    }

    public Object getClassInstance() {
        return classInstance;
    }


    public boolean match(RemoteMethodCall remoteMethodCall) {
        List<String> types = new ArrayList<>(parameterTypes);
        if (parameterTypes.size() == remoteMethodCall.getParameterTypes().size()) {
            for (int i = 0; i < parameterTypes.size(); i++) {
                if (remoteMethodCall.getParameterTypes().get(i) == null) {
                    types.set(i, null);
                }
            }
        }
        if (remoteMethodCall.isCallBySimpleName()) {
            return remoteMethodCall.hashCode() == getClassNameSimple().hashCode() + getMethodName().hashCode() + types.hashCode();
        }
        return remoteMethodCall.hashCode() == getClassName().hashCode() + getMethodName().hashCode() + types.hashCode();
    }

    @Override
    public int hashCode() { //hashCode should match RemoteMethodCall
        return getClassName().hashCode() + getMethodName().hashCode() + parameterTypes.hashCode();
    }

    @Override
    public String toString() {
        return className + "::" + methodName + ", " + parameterTypes.toString();
    }
}
