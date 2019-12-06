package com.company;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import java.sql.*;
import java.util.*;

public class Main {
    private static final String url = "jdbc:postgresql://localhost:32768/swissdomains";
    private static final String user = "postgres";
    private static final String password = "";
    private static final int NUMBER_OF_RESULTS = 10;

    public static void main(String[] args) {
        Map<String, Integer> domains = new TreeMap<>();
        List<String> nameDomains = getAllTable("domains");
        List<String> topDomains = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        System.out.print("Please write your domain name: ");
        String domain_name = sc.nextLine();

        domain_name = domain_name.contains(".ch") ? domain_name : domain_name+".ch";

        setDomainsList(domains, nameDomains, domain_name);


        topDomains = getTop(sortDomains(domains), NUMBER_OF_RESULTS);

        printDomains(topDomains);
    }

    // Function to calculate the Similarity Edit Distance between two strings
    public static int calculateDistance(String a, String b){
        int len_a = a.length();
        int len_b = b.length();

        int[][] dp = new int[len_a + 1][len_b + 1];

        // D(i,0)
        for (int i = 0; i <= len_a; i++) {
            dp[i][0] = i;
        }

        // D(0,j)
        for (int j = 0; j <= len_b; j++) {
            dp[0][j] = j;
        }

        // m1 = D(i,j)
        // m2 = D(i,j+1)
        // m3 = D(i+1,j)
        for (int i = 0; i < len_a; i++) {
            char c1 = a.charAt(i);
            for (int j = 0; j < len_b; j++) {
                char c2 = b.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }
        return dp[len_a][len_b];
    }

    // Function to set the domain names as a Map
    public static void setDomainsList(Map<String, Integer> domains, List<String> nameDomains, String domain_name){
        for( String name: nameDomains){
            domains.put(name, calculateDistance(domain_name, name));
        }
    }

    // Comparator to sort the  domain names Map
    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> sortDomains(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(new Comparator<Map.Entry<K,V>>() {
            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    // Function to get  the Top domain names based on the min similarity distance
    public static List<String> getTop( SortedSet<Map.Entry<String,Integer>> domains, int n){
        List<String> topDomains = new ArrayList<>();
        int control = 0;

        for(Map.Entry<String, Integer> name : domains){
            topDomains.add(name.getKey());
            control++;
            if(control >= n){
               break;
            }
        }
        return  topDomains;
    }

    //Get All elements table
    public static List<String> getAllTable(String tableName){
        Connection conn;
        List<String> domain_names = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM " + tableName ;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String element = rs.getString("name").contains("://") ?
                        rs.getString("name").split("://")[1] : "";
                domain_names.add(element);
            }
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return domain_names;
    }

    // Function to print domain names
    public static void printDomains(List<String> domains){
        System.out.println("The top domain names similar to entered one. ");
        for(String name: domains){
            System.out.printf("* http://%s \n", name);
        }
    }
}


