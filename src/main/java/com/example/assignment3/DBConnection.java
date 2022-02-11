package com.example.assignment3;

import java.sql.*;
import java.util.Objects;


public class DBConnection {


    static private String DB_URL = "jdbc:mariadb://localhost:3300/books";
    static private String USER = "root";
    static private String PASS = "ernie6660";

    public static Connection initDatabase() throws ClassNotFoundException, SQLException {

        Driver d=new org.mariadb.jdbc.Driver();
        DriverManager.registerDriver(d);
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            return conn;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String testQuery = "SELECT * FROM titles";
        Statement stmt = null;
        stmt = Objects.requireNonNull(initDatabase()).createStatement();
        ResultSet rs = stmt.executeQuery(testQuery);
        while(rs.next()){
            System.out.println(rs.getString(2) + " | " + rs.getString(4));
        }
    }
}
