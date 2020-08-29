package org.affluentproductions.jdabase;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.affluentproductions.jdabase.api.AffluentVoteSystem;
import org.affluentproductions.jdabase.console.AffluentConsole;
import org.affluentproductions.jdabase.database.Database;
import org.affluentproductions.jdabase.exception.JDABException;
import org.affluentproductions.jdabase.manager.CommandManager;
import org.affluentproductions.jdabase.manager.EventManager;

public interface JDABaseImpl {

    Database getDatabase();

    AffluentConsole getConsole();

    EventManager getEventManager();

    CommandManager getCommandManager();

    void loadTopGGSetup() throws JDABException;

    void startVoteServer() throws JDABException;

    ShardManager getShardManager();

    AffluentVoteSystem getVoteSystem();
}