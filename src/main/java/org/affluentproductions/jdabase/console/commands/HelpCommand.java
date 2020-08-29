package org.affluentproductions.jdabase.console.commands;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.console.ConsoleCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class HelpCommand extends ConsoleCommand {

    public HelpCommand() {
        super("help", "Lists all available commands.");
    }

    @Override
    public void run(String[] args, JDABase jdaBase) {
        TreeMap<String, ConsoleCommand> commands = new TreeMap<>(jdaBase.getConsole().getCommands());
        StringBuilder response = new StringBuilder();
        List<ConsoleCommand> sortedCommands = new ArrayList<>();
        for (ConsoleCommand consoleCommand : commands.values())
            if (!sortedCommands.contains(consoleCommand)) sortedCommands.add(consoleCommand);
        for (ConsoleCommand cc : sortedCommands) {
            response.append("- ").append(cc.getName()).append(" | ").append(cc.getDescription()).append("\n");
        }
        System.out.println("Commands:\n" + response);
    }
}