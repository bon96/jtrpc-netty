package org.bonbom.communication;

import java.io.Serializable;

/**
 * Tommi
 * Date: 5.5.2019
 * Time: 14.21
 */

public interface RemoteObject extends Serializable {
    String getReceiverName();
}
