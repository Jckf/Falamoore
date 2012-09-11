package com.falamoore.plugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    private Connection connection = null;
    private final String hostname, portnmbr, database, username, password;

    public MySQL(String hostname, String portnmbr, String database, String username, String password) {
        this.hostname = hostname;
        this.portnmbr = portnmbr;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection open() {
        String url = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database;
            this.connection = DriverManager.getConnection(url, this.username, this.password);
            return this.connection;
        } catch (final SQLException e) {
            System.out.print("Could not connect to MySQL server!");
        } catch (final ClassNotFoundException e) {
            System.out.print("JDBC Driver not found!");
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public boolean insert(String table, String[] column, Object[] value) {
        Statement statement = null;
        final StringBuilder sb1 = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        for (final String s : column) {
            sb1.append(s).append(",");
        }
        for (final Object s : value) {
            if (s.toString().matches("0-9")) {
                sb2.append(s.toString());
            } else {
                sb2.append("'").append(s.toString()).append("',");
            }
        }
        final String columns = sb1.toString().substring(0, sb1.toString().length() - 1);
        final String values = sb2.toString().substring(0, sb2.toString().length() - 1);
        try {
            statement = this.connection.createStatement();
            statement.execute("INSERT INTO " + table + "(" + columns + ") VALUES (" + values + ")");
            return true;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet query(String query) throws SQLException {
        Statement statement = null;
        ResultSet result = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            return result;
        } catch (final SQLException e) {
            if (e.getMessage().equals("Can not issue data manipulation statements with executeQuery().")) {
                try {
                    statement.executeUpdate(query);
                } catch (final SQLException ex) {
                    if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                        String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
                        temp = temp.substring(0, temp.lastIndexOf("'"));
                        throw new SQLException(temp);
                    } else {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
                temp = temp.substring(0, temp.lastIndexOf("'"));
                throw new SQLException(temp);
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }
}
