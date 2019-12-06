package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Main{

    private final String url = "jdbc:postgresql://localhost:32768/bayers";
    private final String user = "postgres";
    private final String password = "";
    private final HashMap<String, String> sqlCreate = new HashMap();

    public Main(){
        this.sqlCreate.put("STUDENT", "CREATE TABLE IF NOT EXISTS STUDENT (ID TEXT NOT NULL, " +
                "FIRSTNAME TEXT NOT NULL, LASTNAME TEXT NOT NULL, AGE INT NOT NULL, TYPE TEXT NOT NULL," +
                " PRIMARY KEY(ID))");

        this.sqlCreate.put("LECTURER", "CREATE TABLE IF NOT EXISTS LECTURER (ID TEXT NOT NULL, " +
                "FIRSTNAME TEXT NOT NULL, LASTNAME TEXT NOT NULL, AGE INT NOT NULL, TYPE TEXT NOT NULL," +
                "PRIMARY KEY(ID))");
    }

    // Random generators

    // Method to get string randomly
    private String get_RandomString(int len) {
        Random r = new Random();
        String possibleLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(possibleLetters.charAt(r.nextInt(possibleLetters.length())));
        return sb.toString();
    }

    // Method to get string randomly
    private String get_RandomNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(9999999);

        return String.format("%07d", number);
    }

    // Method to get string randomly
    private int get_RandomAge(int meanValue, int deviation) {
        Random r = new Random();
        int val = (int) Math.abs(Math.round(r.nextGaussian() * deviation) + meanValue);
        return val;
    }


    // Database methods

    //Create table
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

    //Load table
    public void LoadTable(List<Person> persons, String tableName){
        Connection conn;
        ResultSet results = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            String sql = String.format("INSERT INTO %s (id, firstname, lastname, age, type)" +
                    " VALUES (?,?,?,?,?)", tableName);
            PreparedStatement stmt = conn.prepareStatement(sql);
            int count = 0;
            for (Person person : persons) {
                stmt.setString(1, person.getId());
                stmt.setString(2, person.getFirstName());
                stmt.setString(3, person.getLastName());
                stmt.setInt(4, person.getAge());
                stmt.setString(5, tableName);

                stmt.addBatch();
                count++;
                // execute every 100 rows or less
                if (count % 100 == 0 || count == persons.size()) {
                    stmt.executeBatch();
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Delete table
    public void DeleteTable(String tableName){
        Connection conn;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            String sql = "DROP TABLE "+tableName;

            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Get All elements table
    public List<Person> getAllTable(String tableName){
        Connection conn;
        List<Person> persons = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM " + tableName ;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                persons.add(new Person(
                        rs.getString("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getInt("age"),
                        rs.getString("type")));
            }
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return persons;
    }

    // Get Table rows length
    public int tableSize(String tableName){
        Connection conn;
        int rows = 0;
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String sql = "SELECT COUNT(*) FROM "+tableName;
            ResultSet res = stmt.executeQuery(sql);
            while (res.next()){
                rows = res.getInt(1);
            }
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rows;
    }


    // Bayes Classifier methods

    // Calculate prior probability
    public double priorProbability(String tableName, double total){
        double prob = 0;
        prob = tableSize(tableName)/total;
        return prob;
    }

    // Calculate mean value
    public double getMean(String tableName){
        double mean = 0;
        double sum = 0;
        List<Person> persons = getAllTable(tableName);
        for (Person person : persons){
            sum += person.getAge();
        }
        mean = sum / persons.size();
        return mean;
    }

    // Calculate Standard Deviation
    public double getStandarDeviation(String tableName, double mean){
        double sum = 0;
        double sd = 0;
        List<Person> persons = getAllTable(tableName);
        for (Person person : persons){
            sum += Math.pow(person.getAge() - mean, 2);
        }
        sd = sum / persons.size();
        return Math.sqrt(sd);
    }

    // Calculate Likelihood
    public double getLikelihood(int age, double mean, double std_dev){
        double lh = 0;
        double first_part = 1/(std_dev * Math.sqrt(2 * Math.PI));
        double second_part = -(Math.pow((age - mean) / std_dev, 2)) / 2;
        lh = first_part * Math.exp(second_part);

        return lh;
    }

    // Calculate type of person based bayes classifier
    public String calculateType(double probA, double probB){
        String type_person = null;
        type_person = probA > probB ? "Student" : "Lecturer";
        return type_person;
    }


    public void setPersonList(List<Person> persons, int quantity, int mean_age, int deviation, String type){
        for(int i = 0; i < quantity; i++){
            Person person = new Person(get_RandomNumber(), get_RandomString(5),
                    get_RandomString(7), get_RandomAge(mean_age, deviation), type);
            persons.add(person);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int number_of_reps = 100;
        List<Person> students = new ArrayList<>();
        List<Person> lecturers = new ArrayList<>();
        double priorProb_Students = 0;
        double priorProb_Lecturers = 0;
        double mean_Students = 0;
        double mean_Lecturers = 0;
        double stddev_Students = 0;
        double stddev_Lecturers = 0;
        double likelihood_Students = 0;
        double likelihood_Lecturers = 0;

        Main app = new Main();

        // First delete the tables if exist, only for testing purposes, to have different data
        app.DeleteTable("STUDENT");
        app.DeleteTable("LECTURER");

        // Create Tables
        app.CreateTable("STUDENT");
        app.CreateTable("LECTURER");

        // Generate list of Persons with random data
        app.setPersonList(students, 200, 22, 5, "Student");
        app.setPersonList(lecturers, 150, 45, 10, "Lecturer");

        // Load Data in tables
        app.LoadTable(students, "STUDENT");
        app.LoadTable(lecturers, "LECTURER");

        // Total of register including both tables
        double total = students.size() + lecturers.size();

        // prior Probabilities
        priorProb_Students = app.priorProbability("STUDENT", total);
        priorProb_Lecturers = app.priorProbability("LECTURER", total);

        // mean value ages
        mean_Students = app.getMean("STUDENT");
        mean_Lecturers = app.getMean("LECTURER");

        // Standard Deviation
        stddev_Students = app.getStandarDeviation("STUDENT", mean_Students);
        stddev_Lecturers = app.getStandarDeviation("LECTURER", mean_Lecturers);

        // Execution of 100 random persons chosen randomly of the both tables
        int errors = 0; // get the missmatch errors quantity
        for(int i = 0; i< number_of_reps; i++){
            Random r = new Random();
            String[] tables = {"STUDENT", "LECTURER"};
            // Random Table
            String table = tables[r.nextInt(2)];

            List<Person> persons = app.getAllTable(table);
            // Random Person
            Person person = persons.get(r.nextInt(persons.size()));

            // Likelihood
            likelihood_Students = app.getLikelihood(person.getAge(), mean_Students, stddev_Students);
            likelihood_Lecturers = app.getLikelihood(person.getAge(), mean_Lecturers, stddev_Lecturers);

            // Calculate the Bayes classifier
            String type_of_person= app.calculateType(likelihood_Students * priorProb_Students, likelihood_Lecturers * priorProb_Lecturers);

            if(!type_of_person.toUpperCase().equals(table)){
                errors++;
            }
            System.out.printf("The person %s belongs to the table %s - has %s years old." +
                    " The pattern classifier says : This person is a %s \n",
                    person.getFirstName(), table,  person.getAge(), type_of_person);
        }

        System.out.println("*** Quantity of Errors in the process: "+ errors);
        System.out.printf("*** Efficiency of the process: %s", (number_of_reps - errors) * 100/number_of_reps);
    }
}