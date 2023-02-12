package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 * 
 * This is an example JDBC Connection that has a single query for the Movies Database
 * This is similar to the project workshop JDBC examples.
 *
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 */
public class JDBCConnection {

    // Name of database file (contained in database folder)
    private static final String DATABASE = "jdbc:sqlite:database/homeless.db";

    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    public ArrayList<String> getLGAs(ArrayList<Boolean> isChecked, ArrayList<String> cbVals) {
        ArrayList<String> lgas = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT lga_name, state_name from lga";

            Boolean isFirstTrue = true;


            for (int i = 0; i < isChecked.size(); i++) {
                if (isChecked.get(i) && !isFirstTrue) {
                    query += " OR state_name = \"" + cbVals.get(i) + "\"";
                }
                if (isChecked.get(i) && isFirstTrue) {
                    query += " WHERE state_name = \"" + cbVals.get(i) + "\"";
                    isFirstTrue = false; 
                }
            }

            query += ";";

            System.out.println(query);

            ResultSet results = statement.executeQuery(query);
                                                                                                 
            while (results.next()) {
                String lga = results.getString("lga_name");
                String state = results.getString("state_name");
                lgas.add(lga);
                lgas.add(state);
            }

            statement.close();
            
        } catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        return lgas;
    }

    public ArrayList<String> getPopulation(ArrayList<Boolean> isChecked, ArrayList<String> cbVals, HashMap<Integer, Boolean> isInAgeRange, ArrayList<String> states, ArrayList<Boolean> statesChecked, ArrayList<String> lgas, ArrayList<Boolean> lgasChecked, String sortbyVal) {
         ArrayList<String> populations = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);


            String query = "";

            switch (sortbyVal) {
                case "lgas": query += "SELECT p.lga_year, l.lga_name, l.state_name, p.type_of_people, p.gender, p.age_range, SUM(p.population) " +
                "FROM populatesin p " +
                "JOIN lga l ON l.lga_code = p.lga_code AND l.lga_year = p.lga_year " +
                "WHERE p.lga_year = 2018 ";
                                break;
                case "states": query += "SELECT p.lga_year, l.state_name, p.type_of_people, p.gender, p.age_range, SUM(p.population) " +
                "FROM populatesin p " +
                "JOIN lga l ON l.lga_code = p.lga_code AND l.lga_year = p.lga_year " +
                "WHERE p.lga_year = 2018 ";
                                break;
                default: query += "SELECT p.lga_year, l.lga_name, l.state_name, p.type_of_people, p.gender, p.age_range, SUM(p.population) " +
                "FROM populatesin p " +
                "JOIN lga l ON l.lga_code = p.lga_code AND l.lga_year = p.lga_year " +
                "WHERE p.lga_year = 2018 ";
                                break;
             }                         

            for (int i = 0; i < isChecked.size(); i++) {
                System.out.println("checked is " + isChecked.get(i));
            }

            // for (int i = 0; i < isChecked.size(); i++) {
            //     // If any of the checkboxes is unchecked
            //     if (!isChecked.get(i)) {
            //         uncheckedIndex.add(i);
            //         isUnchecked = true; 
            //     }
            // }

            // for (int i = 0; i < uncheckedIndex.size(); i++) {
            //     System.out.println("unchecked is " + uncheckedIndex.get(i));
            // }

            // If one of the radio buttons is unselected
            Boolean bothGroupSelected = isChecked.get(0);
            Boolean bothGenderSelected = isChecked.get(3);

            if (!bothGroupSelected || !bothGenderSelected) {
                if (isChecked.get(1)) {
                    query += " AND p.type_of_people = 'homeless'";
                }
                if (isChecked.get(2)) {
                    query += " AND p.type_of_people = 'at_risk'";
                }
                if (isChecked.get(4)) {
                    query += " AND p.gender = 'male'";
                }
                if (isChecked.get(5)) {
                    query += " AND p.gender = 'female'";
                }
            }
        
            // Sort the isINAgeRange 
            TreeMap<Integer, Boolean> sortedIsInAgeRange = new TreeMap<>();
            sortedIsInAgeRange = sortByKey(isInAgeRange);

            int j = 0;

            for (int i = 0; j < sortedIsInAgeRange.size() - 1; i += 10) {
                System.out.println("sorted Range value is" + sortedIsInAgeRange.get(i));
                if (!sortedIsInAgeRange.get(i) || !sortedIsInAgeRange.get(i + 10)) {
                    if (i == 60) {
                        query += " AND p.age_range != '" + i + "+" + "'";    
                    } else {
                        query += " AND p.age_range != '" + i + "_" + (i + 10) + "'";    
                    }
                    
                }
                j++;
            }

            

            // Filter out by states
            if (statesChecked.size() > 0) {
                for (int i = 0; i < statesChecked.size(); i ++) {
                    if (i == 0) {
                        query += " AND (l.state_name = '" + states.get(i) + "'"; 
                    } else {
                        query += " OR l.state_name = '" + states.get(i) + "'"; 
                    }
                    
                }
                query += ")";
            }


            // Filter out by states
            if (lgasChecked.size() > 0) {
                for (int i = 0; i < lgasChecked.size(); i ++) {
                    if (i == 0) {
                        query += " AND (l.lga_name = '" + lgas.get(i) + "'"; 
                    } else {
                        query += " OR l.lga_name = '" + lgas.get(i) + "'"; 
                    }
                    
                }
                query += ")";
            }

            switch (sortbyVal) {
                case "lgas": query += "  GROUP BY l.lga_name;";
                                break;
                case "states": query += "  GROUP BY l.state_name;";
                                break;
                default: query += "  GROUP BY l.lga_name;";
                                break;
            }


            System.out.println(query);

            

            ResultSet results = statement.executeQuery(query);

            if (sortbyVal.equals("lgas")) {
                while (results.next()) {

                    String year = results.getString("lga_year");
                    String lgaName = results.getString("lga_name");
                    String stateName = results.getString("state_name");
                    String type = results.getString("type_of_people");
                    String gender = results.getString("gender");
                    String age = results.getString("age_range");
                    String population = results.getString("SUM(p.population)");
                    populations.add(year);
                    populations.add(lgaName);
                    populations.add(stateName);
                    populations.add(type);
                    populations.add(gender);
                    populations.add(age);
                    populations.add(population);
                
                    
                }
            } else if (sortbyVal.equals("states")) {
                while (results.next()) {

                    String year = results.getString("lga_year");
                    String stateName = results.getString("state_name");
                    String type = results.getString("type_of_people");
                    String gender = results.getString("gender");
                    String age = results.getString("age_range");
                    String population = results.getString("SUM(p.population)");
                    populations.add(year);
                    populations.add(stateName);
                    populations.add(type);
                    populations.add(gender);
                    populations.add(age);
                    populations.add(population);
                
                    
                }
            }
                                                                                                 


            // for (String population : populations) {
            //     System.out.println(population);
            // }

            statement.close();
            
        } catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        return populations;
    }

    public static TreeMap<Integer, Boolean> sortByKey(HashMap<Integer, Boolean> mapToSort) {
        TreeMap<Integer, Boolean> sortedMap = new TreeMap<>();
        sortedMap.putAll(mapToSort);
        return sortedMap;
    }

}
