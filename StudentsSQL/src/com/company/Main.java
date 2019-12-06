package com.company;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;

public class Main{

    private final String url = "jdbc:postgresql://localhost:32768/skl";
    private final String user = "postgres";
    private final String password = "";
    private final HashMap<String, String> sqlCreate = new HashMap();

    public Main(){
        this.sqlCreate.put("UNIVERSITY", "CREATE TABLE IF NOT EXISTS UNIVERSITY (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, REG_START DATE NOT NULL, REG_END DATE NOT NULL, WEBSITE TEXT NOT NULL, " +
                "PRIMARY KEY(ID))");

        this.sqlCreate.put("PROGRAM", "CREATE TABLE IF NOT EXISTS PROGRAM (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, PRIMARY KEY(ID))");

        this.sqlCreate.put("COURSE", "CREATE TABLE IF NOT EXISTS COURSE (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, PROGRAM INT NOT NULL, DATE_EXAM DATE NOT NULL, REG_DATE_EXAM DATE NOT NULL, " +
                "PRIMARY KEY(ID), FOREIGN KEY (PROGRAM) REFERENCES PROGRAM (ID))");

        this.sqlCreate.put("COURSE_PART", "CREATE TABLE IF NOT EXISTS COURSE_PART (ID INT NOT NULL, " +
                "COURSE INT NOT NULL, NAME TEXT NOT NULL, CONSTRAINT PK_COURSE_PART PRIMARY KEY(ID, COURSE))");

        this.sqlCreate.put("STUDENT", "CREATE TABLE IF NOT EXISTS STUDENT (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, HOST_UNIVERSITY INT NOT NULL, COURSE INT NOT NULL, " +
                "REGISTERED BOOLEAN, PRIMARY KEY(ID), FOREIGN KEY (HOST_UNIVERSITY) REFERENCES UNIVERSITY (ID)," +
                " FOREIGN KEY (COURSE) REFERENCES COURSE (ID))");

        this.sqlCreate.put("LECTURER", "CREATE TABLE IF NOT EXISTS LECTURER (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, JOB_UNIVERSITY INT NOT NULL, COURSE INT NOT NULL, COURSE_PART INT NOT NULL, " +
                "PRIMARY KEY(ID), CONSTRAINT FK_LECTURER FOREIGN KEY(COURSE_PART, COURSE) " +
                " REFERENCES COURSE_PART(ID, COURSE))");
    }


    public void CreateTable(String tableName){
        Connection conn;
        Statement stmt;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database server successfully.");

            stmt = conn.createStatement();
            String sql = sqlCreate.get(tableName);
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("Table created successfully: "+tableName);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getData(){
        Connection conn;
        Statement stmt;
        ResultSet results = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.format("\n*********************\nGetting data from %s \n", conn.getSchema());

            stmt = conn.createStatement();
            String sql = "SELECT s.id, s.name, s.registered, u.name as university, u.website, u.reg_end" +
                    " FROM student AS s, university AS u" +
                    " WHERE s.host_university = u.id" +
                    " ORDER BY u.id";
            results = stmt.executeQuery(sql);
            printResult(results);
            stmt.close();
            System.out.println("Data retrieved successfully ");
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public void printResult(ResultSet result) throws SQLException {
        while (result.next())
        {
            int id = result.getInt("id");
            String name = result.getString("name");
            boolean registered = result.getBoolean("registered");
            String university = result.getString("university");
            String website = result.getString("website");
            Date date = result.getDate("reg_end");

            // print the results
            System.out.format("%s, %s, %s, %s, %s, %s\n", id, name, registered, university, website, date);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.CreateTable("UNIVERSITY");
        app.CreateTable("PROGRAM");
        app.CreateTable("COURSE");
        app.CreateTable("COURSE_PART");
        app.CreateTable("STUDENT");
        app.CreateTable("LECTURER");

        // Execute the sql query to get data
        app.getData();
    }
}