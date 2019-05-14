package org.bonbom;

import org.bonbom.communication.ReleaseCall;

public interface Remote {

    int getId();

    NetworkNode getNode();

    String getOwner();

    default void finalize() {
        getNode().send(new ReleaseCall(getOwner(), getId()));
    }

}
