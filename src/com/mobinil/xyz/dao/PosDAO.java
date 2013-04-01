/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.dao;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Record;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gado
 */
public class PosDAO {

    public static boolean isExist(Connection connection, String posCode) {
        // SANDLogger.getLogger().info("isExist....Start");

        posCode = posCode.trim();
        posCode = posCode.replaceAll(" ", "");
        boolean isExist = false;

        try {
            Statement stat;
            stat = connection.createStatement();
            String sql = "SELECT * FROM NEW_XYZ_BULK_AV_POS WHERE ID='" + posCode + "'";
            ResultSet res = stat.executeQuery(sql);

            if (res.next()) {
                isExist = true;
            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(RecordDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        // SANDLogger.getLogger().info("isExist....ended");

        return isExist;
    }
}
