/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.dao;

import com.mobinil.xyz.model.Record;
import com.mobinil.xyz.util.DBUtil;
import com.mobinil.xyz.util.StatusManager;
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
public class RecordDAO {

    public static List<Record> checkSims(Connection connection, List<Record> recordListCheck) {
        //   SANDLogger.getLogger().info("checkSims....Start");

        List<Record> recordListProcessed = new ArrayList<Record>();


        try {

            Statement stat;
            stat = connection.createStatement();
            ResultSet res = null;
            ResultSet resSFR = null;
            String sql = "";
            String sqlSFR = "";

            for (Record record : recordListCheck) {

                sql = "SELECT ID FROM NEW_XYZ_BULK_RECORD WHERE SIMSERIAL='" + record.getSimSerial() + "' AND DIALNUMBER='" + record.getDialNumber() + "'";
                //   System.out.println("SQL:" + sql);
                res = stat.executeQuery(sql);
                if (res.next()) {
                    record.setStatusId(StatusManager.SIM_SERIAL_ALREADY_EXISTS);
                } else if (record.getStatusId() == 0) {
                    record.setStatusId(StatusManager.TO_BE_PROCESSED);
                }

                sqlSFR = "select sim_serial from SFR_SIM sim where sim.sim_serial = '" + record.getSimSerial() + "' and sim.sim_status_type_id='1'";
                //      System.out.println(sqlSFR);

                resSFR = stat.executeQuery(sqlSFR);
                if (resSFR.next()) {
                    record.setStatusId(StatusManager.SIM_SERIAL_ALREADY_EXISTS);
                } else if (record.getStatusId() == 0) {
                    record.setStatusId(StatusManager.TO_BE_PROCESSED);
                }

                //   System.out.println(record.getStatusId());

                recordListProcessed.add(record);

            }
            res.close();
            resSFR.close();
            stat.close();


        } catch (SQLException ex) {
            Logger.getLogger(RecordDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //    SANDLogger.getLogger().info("checkSims....ended");

        return recordListProcessed;
    }

    public static void saveRecord(Connection connection, Record record) {
        //    SANDLogger.getLogger().info("saveRecord....Start");

        try {
            Statement stat = connection.createStatement();

            Long id = DBUtil.getNextVal(connection, "ID", "NEW_XYZ_BULK_RECORD");

            StringBuilder sqlString = new StringBuilder();
            sqlString.append("INSERT INTO NEW_XYZ_BULK_RECORD (ID,DIALNUMBER,SIMSERIAL,MAIL_ID,STATUS_ID) VALUES(");
            sqlString.append(id).append(",");
            sqlString.append("'").append(record.getDialNumber()).append("',");
            sqlString.append("'").append(record.getSimSerial()).append("',");
            sqlString.append("'").append(record.getMailId()).append("',");
            sqlString.append("decode (");
            sqlString.append(record.getStatusId());
            sqlString.append(",");
            sqlString.append("1 , (decode((select count(*) from NEW_XYZ_BULK_RECORD WHERE");
            sqlString.append(" SIMSERIAL='").append(record.getSimSerial()).append("'");
            sqlString.append(" AND DIALNUMBER ='").append(record.getDialNumber()).append("')");
            sqlString.append(", 0 , 1, 18  )) ,");
            sqlString.append(record.getStatusId()).append("))");
            //   System.out.println(sqlString.toString());
            stat.executeUpdate(sqlString.toString());
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, "Unable to access database while saving record", ex);
        }

        //  SANDLogger.getLogger().info("saveRecord....ended");

    }

    public static List<Record> getRecordsByEmailId(Connection connection, Long emailId) {
        //    SANDLogger.getLogger().info("getRecordsByMailId....Start");

        List<Record> records = new ArrayList<Record>();

        try {

            Statement stat;

            stat = connection.createStatement();
            String sql = "SELECT * FROM NEW_XYZ_BULK_RECORD WHERE MAIL_ID=" + emailId;
            ResultSet res = stat.executeQuery(sql);

            while (res.next()) {

                Record record = new Record(res);

                records.add(record);

            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(RecordDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //  SANDLogger.getLogger().info("getRecordsByMailId....ended");

        return records;
    }

    public List<Boolean> alreadyExistsWithSamePOS(Connection connection, List<Record> records, String pos) {
//        SANDLogger.getLogger().info("alreadyExistsWithSamePOS....Start");
        List<Boolean> resultList = new ArrayList<Boolean>();
        String foundPos = pos, simSerial = "", dial = "";
        long id = 0;
        try {
            Statement stat;
            stat = connection.createStatement();
            ResultSet res = null;
            ResultSet resSFR = null;
            for (Record record : records) {

                simSerial = record.getSimSerial();
                dial = record.getDialNumber();
                id = record.getId();
                String sql = "SELECT * FROM NEW_XYZ_BULK_RECORD,NEW_XYZ_BULK_EMAIL WHERE NEW_XYZ_BULK_RECORD.MAIL_ID=NEW_XYZ_BULK_EMAIL.ID AND NEW_XYZ_BULK_EMAIL.POSCODE='" + pos + "' AND NEW_XYZ_BULK_RECORD.SIMSERIAL='" + simSerial + "' AND NEW_XYZ_BULK_RECORD.DIALNUMBER='" + dial + "' AND NEW_XYZ_BULK_RECORD.ID<>" + id;
                res = stat.executeQuery(sql);
                if (res.next()) {
                    foundPos = res.getString("POSCODE");
                    resultList.add(foundPos.equals(pos));

                } else {
                    String sqlSFR = "select DCM_CODE ,sim_serial from SFR_SIM sim, SFR_SHEET sheet, gen_dcm dcm where sim.SHEET_ID =sheet.SHEET_ID and sheet.POS_ID = dcm.DCM_ID and sim.sim_status_type_id='1' and sim.sim_serial = '" + simSerial + "'";
                    resSFR = stat.executeQuery(sqlSFR);
                    if (resSFR.next()) {
                        foundPos = res.getString("DCM_CODE");
                        resultList.add(foundPos.equals(pos));

                    }
                }


            }


            res.close();
            if (resSFR != null) {
                resSFR.close();
            }
            stat.close();

        } catch (Exception ex) {
            Logger.getLogger(RecordDAO.class.getName()).log(Level.SEVERE, null, ex);
        }


//        SANDLogger.getLogger().info("alreadyExistsWithSamePOS....ended");

        return resultList;
    }
}
