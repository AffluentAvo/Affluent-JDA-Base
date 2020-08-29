package org.affluentproductions.jdabase.console;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.console.commands.HelpCommand;
import org.affluentproductions.jdabase.console.commands.OSICommand;
import org.affluentproductions.jdabase.console.commands.StopCommand;
import org.affluentproductions.jdabase.exception.JDABException;
import org.affluentproductions.jdabase.thread.AffluentThread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class AffluentConsole {

    private static boolean intialized = false;
    private final HashMap<String, ConsoleCommand> commands = new HashMap<>();
    private final JDABase jdaBase;

    public AffluentConsole(JDABase jdaBase) throws JDABException {
        this.jdaBase = jdaBase;
        if (intialized)
            throw new JDABException("A console has already been initialized - you can not run more of them!");
        intialized = true;
        registerCommands(new OSICommand(), new StopCommand(), new HelpCommand());
    }

    /**
     * Register a command for the console.
     *
     * @param consoleCommands Registers this {@link ConsoleCommand}.
     */
    public void registerCommands(ConsoleCommand... consoleCommands) {
        for (ConsoleCommand consoleCommand : consoleCommands)
            commands.put(consoleCommand.getName().toLowerCase(), consoleCommand);
    }

    /**
     * @return A copy of the commands-{@link HashMap}
     */
    public HashMap<String, ConsoleCommand> getCommands() {
        return new HashMap<>(commands);
    }

    /**
     * Start the console
     * <p>
     * Default Commands:
     * <ul>
     *     <li>help</li> - Lists all available commands
     *     <li>osi</li> - OS information (CPU etc.)
     *     <li>stop</li> - Shut downs the bot, stops all threads
     * </ul>
     */
    public void startConsole() {
        Thread thread = new Thread(() -> {
            Scanner s = new Scanner(System.in);
            while (s.hasNextLine()) {
                String[] command = s.nextLine().split("\\s+");
                String name = command[0].toLowerCase();
                String[] args = Arrays.copyOfRange(command, 1, command.length);
                processCommand(name, args);
            }
        });
        AffluentThread affluentThread = new AffluentThread(thread);
        jdaBase.addThread(affluentThread);
        System.out.println("[INFO] Started console with " + commands.size() + " commands.");
    }

    private void processCommand(String cmd, String[] args) {
        if (commands.containsKey(cmd.toLowerCase())) {
            commands.get(cmd.toLowerCase()).run(args, jdaBase);
        }
        if (cmd.equalsIgnoreCase("stop")) {
            System.out.println("[INFO] Received shutdown command");
            boolean force = false;
            for (String arg : args)
                if (arg.equals("-f")) {
                    force = true;
                    break;
                }
            if (force) {
                System.out.println("[INFO] Force exiting program...");
                System.exit(0);
            }
            jdaBase.shutdown();
        }
    }

}
