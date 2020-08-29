package org.affluentproductions.jdabase.utils;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.AffluentAdapter;
import org.affluentproductions.jdabase.database.Database;
import org.affluentproductions.jdabase.event.AffluentPostLoadEvent;

import java.sql.SQLException;
import java.util.HashMap;

public class CooldownUtil extends AffluentAdapter {

    private final HashMap<String, HashMap<String, Long>> userCooldowns = new HashMap<>();
    private final JDABase jdaBase;

    public CooldownUtil(JDABase jdaBase) {
        this.jdaBase = jdaBase;
        jdaBase.getEventManager().addListener(this);
    }

    @Override
    public void onAffluentPostLoadEvent(AffluentPostLoadEvent event) {

    }

    /**
     * @param userId       User ID to sort the cooldown to a user.
     * @param cooldownName Cooldown name
     * @param cooldownEnd  Time in milliseconds when the cooldown ends.
     * @param database     Specify to store cooldown in database too.
     *                     Long cooldowns should be stored in database to prevent them getting lost through bot
     *                     restarts.
     */
    public void setUserCooldown(String userId, String cooldownName, long cooldownEnd, boolean database) {
        HashMap<String, Long> cooldowns = userCooldowns.getOrDefault(userId, new HashMap<>());
        cooldowns.put(cooldownName, cooldownEnd);
        userCooldowns.put(userId, cooldowns);
        if (database) {
            Database db = jdaBase.getDatabase();
            try {
                if (db != null && db.isConnected()) {
                    db.update("DELETE FROM jdb_cooldowns WHERE userId=? AND cooldownName=?;", userId, cooldownName);
                    db.update("INSERT INTO jdb_cooldowns VALUES (?, ?, ?);", userId, cooldownName,
                              String.valueOf(cooldownEnd));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param userId       User ID of sorted cooldown
     * @param cooldownName Cooldown name
     * @return {@code true} if user has an active cooldown, {@code false} if cooldown expired or is not stored.
     */
    public boolean hasUserCooldown(String userId, String cooldownName) {
        return getUserCooldownEnd(userId, cooldownName) != -1L;
    }

    /**
     * @return Time in milliseconds when the cooldown ends. Returns {@code -1} if cooldown expired or is not stored.
     */
    public long getUserCooldownEnd(String userId, String cooldownName) {
        long end = userCooldowns.getOrDefault(userId, new HashMap<>()).getOrDefault(cooldownName, -1L);
        if (end > System.currentTimeMillis()) return end;
        return -1;
    }
}
