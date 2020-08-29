package org.affluentproductions.jdabase.config;

public class JDABaseConfig {

    private String topgg_token = null;
    private String topgg_vote_auth = null;
    private String token = null;
    private long topgg_period = 600000;
    private int voteServerPort = 5000;
    private int shards = 1;

    /**
     * Initiate a JDABaseConfig
     */
    public JDABaseConfig() {
    }

    /**
     * Set the token of your bot for the JDA base
     *
     * @param token value of your bots token
     * @return Current JDABaseConfig object using the given token
     */
    public JDABaseConfig setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * Set the shard amount for the bot
     *
     * @param shards amount of shards
     * @return Current JDABaseConfig object using the given shard amount
     */
    public JDABaseConfig setShards(int shards) {
        this.shards = shards;
        return this;
    }

    /**
     * Set the token for top.gg API
     *
     * @param topgg_token value of your bots top.gg token
     * @return Current JDABaseConfig object using the given top.gg token
     */
    public JDABaseConfig setTopGGToken(String topgg_token) {
        this.topgg_token = topgg_token;
        return this;
    }

    /**
     * Set the Authentication for the vote api of top.gg
     *
     * @param topgg_vote_auth value of your bots vote authentication
     * @return Current JDABaseConfig object using the given top.gg vote authentication value
     */
    public JDABaseConfig setTopGGVoteAuth(String topgg_vote_auth) {
        this.topgg_vote_auth = topgg_vote_auth;
        return this;
    }

    /**
     * Set the period in milliseconds for stats post timer
     *
     * @param topgg_period repeat time in milliseconds for the stats post timer
     * @return Current JDABaseConfig object using the given timer period
     */
    public JDABaseConfig setTopGGPeriod(long topgg_period) {
        this.topgg_period = topgg_period;
        return this;
    }

    /**
     * Set the port for the vote server
     *
     * @param voteServerPort port for the vote server
     * @return Current JDABaseConfig object using the given vote server port
     */
    public JDABaseConfig setVoteServerPort(int voteServerPort) {
        this.voteServerPort = voteServerPort;
        return this;
    }

    /**
     * @return configured shard amount
     */
    public int getShards() {
        return shards;
    }

    /**
     * @return configured bot token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return configured top.gg token
     */
    public String getTopGGToken() {
        return topgg_token;
    }

    /**
     * @return configured top.gg vote authentication value
     */
    public String getTopGGVoteAuth() {
        return topgg_vote_auth;
    }

    /**
     * @return configured top.gg stats post timer period time
     */
    public long getTopGGPeriod() {
        return topgg_period;
    }

    /**
     * @return configured vote server port
     */
    public int getVoteServerPort() {
        return voteServerPort;
    }
}