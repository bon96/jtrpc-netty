package commands;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 15.54
 */

public class CommandImpl implements Command {
    @Override
    public String output(String string) {
        return string + " 23";
    }
}
