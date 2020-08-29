package org.affluentproductions.jdabase.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.event.AffluentShardLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ShardListener extends ListenerAdapter {

    private final JDABase jdaBase;

    public ShardListener(JDABase jdaBase) {
        this.jdaBase = jdaBase;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        jdaBase.getEventManager().fireEvent(new AffluentShardLoadEvent(jdaBase, event));
    }
}