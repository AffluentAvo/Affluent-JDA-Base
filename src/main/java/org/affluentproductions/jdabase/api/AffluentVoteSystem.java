package org.affluentproductions.jdabase.api;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.database.Database;
import org.affluentproductions.jdabase.event.AffluentPostLoadEvent;
import org.affluentproductions.jdabase.event.AffluentVoteEvent;
import org.affluentproductions.jdabase.event.AffluentVoteExpireEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AffluentVoteSystem extends AffluentAdapter {

    private final HashMap<String, Long> votes = new HashMap<>();
    private final JDABase jdaBase;
    private final ScheduledExecutorService voteChecker = Executors.newSingleThreadScheduledExecutor();

    public AffluentVoteSystem(JDABase jdaBase) {
        this.jdaBase = jdaBase;
        jdaBase.getEventManager().addListener(this);
    }

    @Override
    public void onAffluentPostLoadEvent(AffluentPostLoadEvent event) {
        Database database = jdaBase.getDatabase();
        try {
            if (database != null && database.isConnected()) {
                database.update(
                        "CREATE TABLE IF NOT EXISTS jdb_votes (userId VARCHAR(18) NOT NULL, voteEnd VARCHAR(24) NOT "
                                + "NULL)" + ";");
                long now = System.currentTimeMillis();
                ResultSet rs = database.query("SELECT * FROM jdb_votes;");
                while (rs.next()) {
                    String userId = rs.getString("userId");
                    long end = Long.parseLong(rs.getString("voteEnd"));
                    if (end <= now) {
                        expireVote(userId, end);
                    } else votes.put(userId, end);
                }
                rs.close();
            }
            voteChecker.scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                for (String userId : votes.keySet()) {
                    long end = votes.get(userId);
                    if (end <= now) expireVote(userId, end);
                }
            }, 60 * 1000, 30 * 1000 + 2 * 60 * 1000, TimeUnit.MILLISECONDS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void vote(String userId, boolean weekend) {
        vote(userId);
        jdaBase.getEventManager().fireEvent(new AffluentVoteEvent(jdaBase, userId, weekend));
    }

    public boolean hasVoted(String userId) {
        boolean voted = votes.containsKey(userId);
        if (voted) voted = votes.get(userId) >= System.currentTimeMillis();
        return voted;
    }

    public long getUntilVote(String userId) {
        return votes.getOrDefault(userId, -1L);
    }

    private void vote(String userId) {
        long now = System.currentTimeMillis();
        long t12h = 12 * 60 * 60 * 1000;
        votes.put(userId, now + t12h);
    }

    private void expireVote(String userId, long end) {
        votes.remove(userId);
        jdaBase.getEventManager().fireEvent(new AffluentVoteExpireEvent(jdaBase, userId, end));
        Database database = jdaBase.getDatabase();
        try {
            if (database != null && database.isConnected()) {
                database.update("DELETE FROM jdb_votes WHERE userId=?;", userId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}