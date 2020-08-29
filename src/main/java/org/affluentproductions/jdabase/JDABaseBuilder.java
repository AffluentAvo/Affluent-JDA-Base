package org.affluentproductions.jdabase;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.affluentproductions.jdabase.config.JDABaseConfig;
import org.affluentproductions.jdabase.enums.BuilderStatus;
import org.affluentproductions.jdabase.exception.JDABBuilderException;
import org.affluentproductions.jdabase.exception.JDABException;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class JDABaseBuilder implements JDABaseBuilderImpl {

    private BuilderStatus builderStatus;
    private JDABaseConfig config;
    private final DefaultShardManagerBuilder builder;

    /**
     * Initiate a JDABaseBuilder
     */
    public JDABaseBuilder() {
        config = null;
        builder = DefaultShardManagerBuilder.create(new ArrayList<>());
        builderStatus = BuilderStatus.INITIATED;
    }

    /**
     * Configurate this JDA base.
     *
     * @param jdaBaseConfig A config, which provides the bot token and shard amount.
     */
    public void setConfig(JDABaseConfig jdaBaseConfig) {
        this.config = jdaBaseConfig;
    }

    /**
     * Initiate pre-load of JDA Base
     *
     * @throws JDABBuilderException If no config is set, or {@link JDABaseConfig#getShards()} amount is < 1
     */
    public void preLoad() throws JDABBuilderException {
        // Check if config is set
        if (config == null) throw new JDABBuilderException("JDA base is not configured.");
        // Set bot token
        builder.setToken(config.getToken());
        // Set shards amount
        int shards = config.getShards();
        if (shards < 1) throw new JDABBuilderException("JDA base is configured to have less than one shard.");
        builder.setShardsTotal(shards);
        builderStatus = BuilderStatus.PRELOADED;
    }

    public DefaultShardManagerBuilder getShardManagerBuilder() throws JDABBuilderException {
        if (builderStatus == BuilderStatus.INITIATED) throw new JDABBuilderException("JDA base is not pre-loaded yet.");
        return builder;
    }

    /**
     * Load the JDA base
     *
     * @throws JDABBuilderException if any exceptions are thrown by {@link #preLoad()} or JDA base is loaded already.
     * @see #preLoad()
     */
    public void load() throws JDABBuilderException {
        if (builderStatus == BuilderStatus.INITIATED) preLoad();
        if (builderStatus == BuilderStatus.LOADED) throw new JDABBuilderException("JDA base is already loaded.");
        builderStatus = BuilderStatus.LOADED;
    }

    /**
     * Completes build and starts main JDA base
     *
     * @return JDA base object
     * @throws LoginException if shard manager could not login (invalid token?)
     * @throws JDABException if any JDA Base exceptions are thrown within JDA Base construction
     * @see DefaultShardManagerBuilder#build()
     */
    public JDABase build() throws LoginException, JDABException {
        return new JDABase(builder.build(), config);
    }
}