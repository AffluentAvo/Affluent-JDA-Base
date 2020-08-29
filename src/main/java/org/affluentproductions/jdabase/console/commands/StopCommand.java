package org.affluentproductions.jdabase.console.commands;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.console.ConsoleCommand;

public class StopCommand extends ConsoleCommand {

    public StopCommand() {
        super("stop", "Shutdowns the JDA and its components | -f to force exit");
    }

    @Override
    public void run(String[] args, JDABase jdaBase) {
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
        System.out.println("[INFO] Shutting down JDA Base...");
        jdaBase.shutdown();
    }
}
