package communication;

import java.io.Serializable;

/**
 * Tommi
 * Date: 29.1.2019
 * Time: 0.02
 */

public class SessionRegistrationCall implements Serializable {

    private String name;

    public SessionRegistrationCall(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
