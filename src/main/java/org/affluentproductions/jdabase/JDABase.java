package org.affluentproductions.jdabase;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.affluentproductions.jdabase.api.AffluentAdapter;
import org.affluentproductions.jdabase.api.AffluentVoteSystem;
import org.affluentproductions.jdabase.config.JDABaseConfig;
import org.affluentproductions.jdabase.console.AffluentConsole;
import org.affluentproductions.jdabase.database.Database;
import org.affluentproductions.jdabase.event.AffluentPostLoadEvent;
import org.affluentproductions.jdabase.event.AffluentPreLoadEvent;
import org.affluentproductions.jdabase.event.AffluentShardLoadEvent;
import org.affluentproductions.jdabase.exception.JDABException;
import org.affluentproductions.jdabase.listener.CommandListener;
import org.affluentproductions.jdabase.listener.ShardListener;
import org.affluentproductions.jdabase.manager.CommandManager;
import org.affluentproductions.jdabase.manager.EventManager;
import org.affluentproductions.jdabase.thread.AffluentThread;
import org.affluentproductions.jdabase.utils.CooldownUtil;
import org.affluentproductions.jdabase.vote.VoteServer;
import org.discordbots.api.client.DiscordBotListAPI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JDABase extends AffluentAdapter implements JDABaseImpl {

    private final HashMap<Long, AffluentThread> threads = new HashMap<>();
    private final ScheduledExecutorService mainScheduler;
    private final ShardManager shardManager;
    private final JDABaseConfig config;
    private Database database;
    private final AffluentConsole console;
    private final CommandManager commandManager;
    private final EventManager eventManager;
    private final AffluentVoteSystem voteSystem;
    private final CooldownUtil cooldownUtil;
    private List<Integer> previousGuildCounts = null;
    private DiscordBotListAPI api;

    /**
     * @param shardManager {@link ShardManager} object for this JDA Base
     * @param config       {@link JDABaseConfig} configuration for this JDA Base
     * @throws JDABException if any components throw any errors
     *                       > {@link AffluentConsole}
     */
    protected JDABase(ShardManager shardManager, JDABaseConfig config) throws JDABException {
        this.console = new AffluentConsole(this);
        this.mainScheduler = Executors.newSingleThreadScheduledExecutor();
        this.shardManager = shardManager;
        this.config = config;
        this.eventManager = new EventManager(this, Executors.newSingleThreadExecutor());
        this.commandManager = new CommandManager(this, Executors.newCachedThreadPool());
        voteSystem = new AffluentVoteSystem(this);
        this.cooldownUtil = new CooldownUtil(this);
        this.eventManager.addListener(this);
        this.eventManager.fireEvent(new AffluentPreLoadEvent(this));
    }

    /**
     * @return This JDA Bases Cooldown Util
     */
    public CooldownUtil getCooldownUtil() {
        return cooldownUtil;
    }

    /**
     * @return This JDA Bases Database
     */
    @Override
    public Database getDatabase() {
        return database;
    }

    /**
     * @return This JDA Bases Console
     */
    @Override
    public AffluentConsole getConsole() {
        return console;
    }

    /**
     * @return This JDA Bases Event Manager
     */
    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return This JDA Bases Command Manager
     */
    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return This JDA Bases Vote System
     */
    @Override
    public AffluentVoteSystem getVoteSystem() {
        return voteSystem;
    }

    /**
     * Set the JDA Base Database
     */
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * Setup top.gg API
     *
     * @throws JDABException if top.gg token is not configured
     * @throws JDABException if top.gg period is smaller than 60.000
     */
    public void loadTopGGSetup() throws JDABException {
        String topgg_token = config.getTopGGToken();
        if (topgg_token == null) throw new JDABException("top.gg token is not configured - can not setup top.gg");
        DiscordBotListAPI.Builder dblbuilder = new DiscordBotListAPI.Builder();
        dblbuilder.token(topgg_token);
        shardManager.retrieveApplicationInfo().queue(ai -> {
            dblbuilder.botId(ai.getId());
            api = dblbuilder.build();
        });
        long topgg_period = config.getTopGGPeriod();
        if (topgg_period < 60 * 1000) throw new JDABException("top.gg period can not be smaller than 60000!");
        this.mainScheduler.scheduleAtFixedRate(() -> {
            List<Integer> guildCounts = new ArrayList<>();
            for (JDA shard : shardManager.getShards())
                if (shard.getStatus() == JDA.Status.CONNECTED) guildCounts.add(shard.getGuilds().size());
            if (guildCounts.size() < shardManager.getShardsTotal()) {
                if (previousGuildCounts != null) api.setStats(previousGuildCounts);
                else System.out.println("[INTERN INTO] Skipped top.gg stats post - some shards are offline");
            } else {
                previousGuildCounts = guildCounts;
                api.setStats(guildCounts);
            }
        }, 10 * 60 * 1000, topgg_period, TimeUnit.MILLISECONDS);
    }

    /**
     * Start the vote server for top.gg votes
     *
     * @throws JDABException if top.gg vote authentication value is not configured
     */
    public void startVoteServer() throws JDABException {
        String topgg_vote_auth = config.getTopGGVoteAuth();
        if (topgg_vote_auth == null)
            throw new JDABException("top.gg vote authentication value is not configured - can not start vote server");
        int voteServerPort = config.getVoteServerPort();
        if (voteServerPort > 65535)
            throw new JDABException("Vote Server Port range exceeded - port can not be bigger than 63353");
        Thread voteServerThread = VoteServer.start(this, topgg_vote_auth, voteServerPort);
        AffluentThread affluentVoteServerThread = new AffluentThread(voteServerThread);
        threads.put(affluentVoteServerThread.getId(), affluentVoteServerThread);
    }

    /**
     * @return This JDA Bases {@link ShardManager} object
     */
    public ShardManager getShardManager() {
        return shardManager;
    }

    public void addThread(AffluentThread affluentThread) {
        threads.put(affluentThread.getId(), affluentThread);
    }

    /**
     * Shutdown the JDA, JDA Base and all components
     */
    public void shutdown() {
        System.out.println("[SHUTDOWN] Stopping vote server...");
        VoteServer.stop();
        System.out.println("[SHUTDOWN] Cancelling threads...");
        for (AffluentThread affluentThread : threads.values()) affluentThread.cancel();
        System.out.println("[SHUTDOWN] Shutting down shard manager...");
        shardManager.shutdown();
        System.out.println("[SHUTDOWN] Disconnecting database...");
        try {
            if (database != null && database.isConnected()) database.disconnect();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("Bye!");
        System.exit(0);
    }

    //

    private CommandListener commandListener = null;
    private int shardsLoaded = 0;

    @Override
    public void onAffluentShardLoadEvent(AffluentShardLoadEvent event) {
        shardsLoaded++;
        if (shardsLoaded < shardManager.getShardsTotal()) return;
        getEventManager().fireEvent(new AffluentPostLoadEvent(this));
    }

    /**
     * Use this {@link CommandListener} to configurate command handling
     *
     * @return This JDA Bases {@link CommandListener}. Returns null if not initialized yet, which basically is not
     * really possible.
     * @see CommandListener#setBotsAllowed(boolean)
     */
    public CommandListener getCommandListener() {
        return commandListener;
    }

    @Override
    public void onAffluentPreLoadEvent(AffluentPreLoadEvent event) {
        commandListener = new CommandListener(this);
        shardManager.addEventListener(commandListener);
        shardManager.addEventListener(new ShardListener(this));
    }

    @Override
    public void onAffluentPostLoadEvent(AffluentPostLoadEvent event) {
        System.out.println("[INFO] Fully loaded Affluent System");
    }
}