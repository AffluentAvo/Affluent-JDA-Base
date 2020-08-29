package org.affluentproductions.jdabase.console;

import org.affluentproductions.jdabase.JDABase;

public abstract class ConsoleCommand {

    private final String name;
    private final String description;

    public ConsoleCommand(String name) {
        this(name, "No description provided.");
    }

    public ConsoleCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void run(String[] args, JDABase jdaBase);
}