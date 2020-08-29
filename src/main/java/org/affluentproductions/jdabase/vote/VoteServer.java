package org.affluentproductions.jdabase.vote;

import org.affluentproductions.jdabase.JDABase;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class VoteServer {
    private static ServerSocket serverSocket;
    private static final HashMap<Long, Socket> connections = new HashMap<>();
    private static String vote_auth;

    static void close(long id) {
        Socket socket = connections.get(id);
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Thread start(JDABase jdaBase, String topgg_vote_auth, int serverPort) {
        vote_auth = topgg_vote_auth;
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(serverPort);
                System.out.println("[INTERN INFO] Started Vote Server on port " + serverSocket.getLocalPort());
                load(jdaBase);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        serverThread.setName("Vote-Server-Thread");
        serverThread.setDaemon(true);
        serverThread.start();
        return serverThread;
    }

    private static boolean stopping = false;

    public static void stop() {
        stopping = true;
        try {
            if (serverSocket == null) return;
            for (Socket socket : connections.values()) {
                if (socket != null && !socket.isClosed()) socket.close();
            }
            if (!serverSocket.isClosed()) serverSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static long gcid;

    private static void load(JDABase jdaBase) {
        try {
            while (!serverSocket.isClosed()) {
                try {
                    final Socket socket2 = serverSocket.accept();
                    try {
                        socket2.setSoTimeout(60000);
                        long id = ++gcid;
                        connections.put(id, socket2);
                        VoteClient voteClient = new VoteClient(jdaBase, socket2, socket2.getInputStream(), id);
                        voteClient.process(vote_auth);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    if (stopping) return;
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex2) {
            if (stopping) return;
            ex2.printStackTrace();
        }
    }
}