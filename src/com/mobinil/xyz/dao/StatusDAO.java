/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.dao;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Status;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gado
 */
public class StatusDAO {

    public static Status findStatusById(Connection connection, int statusId) {
//        SANDLogger.getLogger().info("findStatusById....Start");

        Status status = null;
        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT * FROM NEW_XYZ_BULK_STATUS WHERE ID=" + statusId;
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                status = new Status(res);
            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(StatusDAO.class.getName()).log(Level.SEVERE, null, ex);
        }


//        SANDLogger.getLogger().info("findStatusById....ended");

        return status;
    }
}
