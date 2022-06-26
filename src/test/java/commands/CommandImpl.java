package commands;

/**
 * Tommi
 * Date: 3.5.2019
 * Time: 22.58
 */

public class CommandImpl implements Command {

    @Override
    public String sayHiToServer(String msg) {
        System.out.println("Client said " + msg);
        return "Server says hi!";
    }

    @Override
    public void run(SerializableRunner runner) {
        runner.run();
    }
}



