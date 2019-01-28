package communication;

import java.util.HashMap;
import java.util.Map;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 11.46
 */

public class ObjectReceiver {

    private Map<Long, FutureReceive> receives = new HashMap<>();

    public Object get(long id) throws InterruptedException {
        if (!receives.containsKey(id)) {
            receives.put(id, new FutureReceive());
        }
        return receives.get(id).get();
    }

    public void onReceive(RpcAnswer rpcAnswer) {
        if (!receives.containsKey(rpcAnswer.getId())) {
            receives.put(rpcAnswer.getId(), new FutureReceive());
        }
        receives.get(rpcAnswer.getId()).receive(rpcAnswer.getObject());
    }
}
