package org.affluentproductions.jdabase.database;

import java.sql.*;

public class Database {

    private final Connection connection;
    private int stats_updates = 0;
    private int stats_queries = 0;

    public static Database createMySQL(String hostname, int port, String username, String password, String database) throws SQLException {
        return createMySQL(
                "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true&serverTimezone=UTC",
                username, password);
    }

    public static Database createMySQL(String url, String username, String password) throws SQLException {
        return new Database(url, username, password);
    }

    public Database(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public int getStatsUpdates() {
        return stats_updates;
    }

    public int getStatsQueries() {
        return stats_queries;
    }

    public ResultSet query(String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        if (parameters.length > 0) {
            int index = 0;
            for (Object o : parameters) {
                index++;
                ps.setObject(index, o);
            }
        }
        stats_queries++;
        return ps.executeQuery();
    }

    public int update(String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        if (parameters.length > 0) {
            int index = 0;
            for (Object o : parameters) {
                index++;
                ps.setObject(index, o);
            }
        }
        stats_updates++;
        return ps.executeUpdate();
    }
}