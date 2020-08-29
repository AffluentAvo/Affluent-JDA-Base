package org.affluentproductions.jdabase.vote;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.database.Database;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class VoteClient {

    private final JDABase jdaBase;
    private final BufferedReader bufferedReader;
    private final Socket socket;
    private final long id;
    public static boolean lastWeekend = false;

    VoteClient(final JDABase jdaBase, final Socket socket, final InputStream in, final long id) {
        this.jdaBase = jdaBase;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        this.id = id;
    }

    protected void process(final String authentication) {
        try {
            String userId = null;
            boolean authorized = false;
            boolean weekend = false;
            while (socket != null && !socket.isClosed()) {
                final String line = bufferedReader.readLine();
                if (line == null) continue;
                if (line.startsWith("Authorization: ")) {
                    if (line.substring("Authorization: ".length()).equals(authentication)) {
                        authorized = true;
                    }
                }
                if (line.startsWith("{")) {
                    final JSONObject json = new JSONObject(line);
                    userId = json.getString("user");
                    weekend = json.getBoolean("isWeekend");
                    lastWeekend = weekend;
                    //
                    bufferedReader.close();
                    socket.close();
                    break;
                }
            }
            if (authorized) {
                if (userId == null) {
                    System.out.println("[EXTERN ERROR] Could not process authorized vote for connection ID " + id
                                               + " - no user id given");
                    return;
                }
                Database database = jdaBase.getDatabase();
                long now = System.currentTimeMillis();
                long end = now + (12 * 60 * 60 * 1000);
                if (database != null && database.isConnected())
                    database.update("INSERT INTO votes VALUES (?, ?);", userId, String.valueOf(end));
                jdaBase.getVoteSystem().vote(userId, weekend);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            VoteServer.close(id);
        }
    }
}