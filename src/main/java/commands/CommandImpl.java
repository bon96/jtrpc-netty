package commands;

/**
 * Tommi
 * Date: 28.1.2019
 * Time: 15.54
 */

public class CommandImpl implements Command {
    private static int count = 0;
    @Override
    public String output(String string) {
        count++;
        return string + " " + count;
    }
}
