package com.aen.spaceship_fights.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {
    private static Connection con = null;
    private static final String url = "jdbc:mysql://localhost:3306/game_db";
    private static final String user = "root";
    private static final String password = "";


    public static Connection getDBConnection() {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public static void closeDBConnection() {
        try{
            con.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
