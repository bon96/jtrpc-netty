package communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 16.44
 * Thanks alex for the name!
 */


public class RemoteMethodCall implements Serializable {

    private String senderName;
    private String className;
    private String methodName;
    private Object[] objects;
    private List<String> parameterTypes = new ArrayList<>();

    public RemoteMethodCall(String senderName, String className, String methodName, Object... objects) {
        this.senderName = senderName;
        this.className = className;
        this.methodName = methodName;
        this.objects = objects;

        for (Object object : objects) {
            parameterTypes.add(object.getClass().getName());
        }
    }

    public String getSenderName() {
        return senderName;
    }

    public String getClassName() {
        return className;
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

    @Override
    public int hashCode() { //hashCode should match remoteMethod
        return className.hashCode() + methodName.hashCode() + parameterTypes.hashCode();
    }
}
