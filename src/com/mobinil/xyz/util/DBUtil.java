/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.util;

import java.io.File;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author Gado
 */
public class DBUtil {

    private static String connection = "";
    private static String username = "";
    private static String password = "";
    private static String driver = "";

    static {

        try {

            Properties properties = PropertyLoader.loadProperties("jdbc.properties");

            driver = properties.getProperty("database.driver");
            connection = properties.getProperty("database.url");
            username = properties.getProperty("database.user");
            password = properties.getProperty("database.password");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Here in exception " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection objConnection = null;


        try {
            Class.forName(driver);
            objConnection = DriverManager.getConnection(connection, username, password);


        } catch (Exception objExp) {


            objExp.printStackTrace();
        }


        return objConnection;
    }

    public static void closeConnection(Connection argConnection) {
        try {
            argConnection.close();

        } catch (Exception objExp) {
            objExp.printStackTrace();
        }

    }

    public static Long getNextVal(Connection con, String columnName, String tableName) {

        String sql = "SELECT MAX(" + columnName + ")+1 AS max FROM " + tableName;

        Long seqValue = 0L;
        try {
            Statement stmt = con.createStatement();
            ResultSet seqValueRS = stmt.executeQuery(sql);

            while (seqValueRS.next()) {
                seqValue = seqValueRS.getLong("max");
            }

            seqValueRS.close();
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return seqValue;
    }
}
