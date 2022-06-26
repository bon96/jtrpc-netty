package commands;

public interface Command {
     String sayHiToServer(String msg);

     void run(SerializableRunner runner);
}



