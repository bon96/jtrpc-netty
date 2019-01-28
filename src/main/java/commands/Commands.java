package commands;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 18.01
 */

public class Commands {

    public String sendBack(String string) {
        return string;
    }
    public String sendBack(String string1, String string2) {
        return string1 + ", " + string2;
    }

    public void output(String string) {
        System.out.println(string);
    }

    public void test(Object object) {
        System.out.println(object);
    }
}
