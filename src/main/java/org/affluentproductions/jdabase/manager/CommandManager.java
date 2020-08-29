package org.affluentproductions.jdabase.manager;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.AffluentAdapter;
import org.affluentproductions.jdabase.api.command.AffluentCommand;
import org.affluentproductions.jdabase.enums.Load;
import org.affluentproductions.jdabase.event.AffluentPostLoadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CommandManager extends AffluentAdapter {

    private final JDABase jdaBase;
    private final ExecutorService executorService;
    private final HashMap<String, AffluentCommand> registeredCommands = new HashMap<>();
    private final HashMap<Load, List<AffluentCommand>> loadedCommands = new HashMap<>();

    public CommandManager(JDABase jdaBase, ExecutorService executorService) {
        this.jdaBase = jdaBase;
        this.executorService = executorService;
        jdaBase.getEventManager().addListener(this);
    }

    public void loadCommands(AffluentCommand... affluentCommands) {
        for (AffluentCommand affluentCommand : affluentCommands) {
            Load load = affluentCommand.getLoad();
            List<AffluentCommand> toLoad = loadedCommands.getOrDefault(load, new ArrayList<>());
            toLoad.add(affluentCommand);
            loadedCommands.put(load, toLoad);
            if (load == Load.PRELOAD) registerCommand(affluentCommand);
        }
    }

    private void registerCommand(AffluentCommand affluentCommand) {
        String commandName = affluentCommand.getCommandName().toLowerCase();
        if (!registeredCommands.containsKey(commandName)) {
            registeredCommands.put(commandName, affluentCommand);
        } else {
            System.out.println("[WARN] Command \"" + commandName + "\" is already registered");
        }
        for (String commandAlias : affluentCommand.getCommandAliases()) {
            commandAlias = commandAlias.toLowerCase();
            if (registeredCommands.containsKey(commandAlias)) {
                AffluentCommand registeredCommand = registeredCommands.get(commandAlias);
                System.out.println("[WARN] Alias \"" + commandAlias + "\" for command \"" + commandName
                                           + "\" is already registered to \"" + registeredCommand.getCommandName()
                                           + "\"");
                continue;
            }
            registeredCommands.put(commandAlias, affluentCommand);
        }
    }

    public void unregisterCommand(AffluentCommand affluentCommand) {
        String commandName = affluentCommand.getCommandName().toLowerCase();
        registeredCommands.remove(commandName);
        for (String commandAlias : affluentCommand.getCommandAliases()) {
            registeredCommands.remove(commandAlias);
        }
    }

    public AffluentCommand getCommand(String commandName) {
        return registeredCommands.get(commandName.toLowerCase());
    }

    public boolean isCommand(String commandName) {
        return registeredCommands.containsKey(commandName.toLowerCase());
    }

    public JDABase getJDABase() {
        return jdaBase;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public HashMap<String, AffluentCommand> getRegisteredCommands() {
        return registeredCommands;
    }

    @Override
    public void onAffluentPostLoadEvent(AffluentPostLoadEvent event) {
        List<AffluentCommand> postLoadCommands = this.loadedCommands.getOrDefault(Load.POSTLOAD, new ArrayList<>());
        for (AffluentCommand affluentCommand : postLoadCommands) registerCommand(affluentCommand);
    }
}