/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.dao;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Email;
import com.mobinil.xyz.util.DBUtil;
import com.mobinil.xyz.util.StatusManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gado
 */
public class EmailDAO {

    public static Email findByEmailId(Connection connection, String mailId) {
        //   SANDLogger.getLogger().info("findByEmailId....Start");

        Email email = null;

        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT * FROM NEW_XYZ_BULK_EMAIL WHERE EMAILID='" + mailId + "'";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {

                email = new Email(res);

            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //     SANDLogger.getLogger().info("findByEmailId....ended");
        return email;
    }

    public static List<Email> findAllToBeProcessed(Connection connection) {
//        SANDLogger.getLogger().info("findAllToBeProcessed....Start");

        List<Email> emails = new ArrayList<Email>();

        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT * FROM NEW_XYZ_BULK_EMAIL WHERE SENDFINALSTATUS=0 AND STATUS_ID=" + StatusManager.TO_BE_PROCESSED;
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {

                Email email = new Email(res);
                emails.add(email);

            }

            res.close();
            stat.close();


        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }


//        SANDLogger.getLogger().info("findAllToBeProcessed....ended");

        return emails;
    }

    public static List<String> findAllCompletedSuccessfullyMails(Connection connection) {
        // SANDLogger.getLogger().info("findAllCompletedSuccessfullyMails....Start");
        List<String> mailIds = new ArrayList<String>();

        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT EMAILID FROM NEW_XYZ_BULK_EMAIL WHERE SENDFINALSTATUS=0 AND STATUS_ID=" + StatusManager.COMPLETED;
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {

                mailIds.add(res.getString("EMAILID"));

            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //       SANDLogger.getLogger().info("findAllCompletedSuccessfullyMails....ended");
        return mailIds;
    }

    public static List<String> findAllCompletedFailedMails(Connection connection) {
        //  SANDLogger.getLogger().info("findAllCompletedFailedMails....Start");
        List<String> mailIds = new ArrayList<String>();

        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT EMAILID FROM NEW_XYZ_BULK_EMAIL WHERE SENDFINALSTATUS=0 AND STATUS_ID=" + StatusManager.FAILED;
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {

                mailIds.add(res.getString("EMAILID"));

            }
            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //  SANDLogger.getLogger().info("findAllCompletedFailedMails....ended");
        return mailIds;
    }

    public static List<String> findAllCompletedPartialFailedMails(Connection connection) {
        //     SANDLogger.getLogger().info("findAllCompletedPartialFailedMails....Start");
        List<String> mailIds = new ArrayList<String>();

        Statement stat;
        try {
            stat = connection.createStatement();

            String sql = "SELECT EMAILID FROM NEW_XYZ_BULK_EMAIL WHERE SENDFINALSTATUS=0 AND STATUS_ID=" + StatusManager.PARTIALLY_FAILED;
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {

                mailIds.add(res.getString("EMAILID"));

            }

            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        //     SANDLogger.getLogger().info("findAllCompletedPartialFailedMails....ended");
        return mailIds;
    }

    public static void saveMail(Connection connection, Email email) {

        try {
            Statement stmt;
            String sqlString;
            Format formatter;
            String date;
            stmt = connection.createStatement();

            Long id = DBUtil.getNextVal(connection, "ID", "NEW_XYZ_BULK_EMAIL");
            int hasAttachment = 0;

            if (email.isHasAttachment()) {
                hasAttachment = 1;
            }

            int sendFinalStatus = 0;

            if (email.getSendFinalStatus()) {
                sendFinalStatus = 1;
            }

            String subject = email.getSubject().replace("'", "");
            String body = email.getBody().replace("'", "");

            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            date = formatter.format(email.getCreationDate());
            sqlString = "INSERT INTO NEW_XYZ_BULK_EMAIL (ID,SUBJECT,BODY,HTML,CREATIONDATE,EMAILID,HASATTACHMENT,SENDERNAME,SENDEREMAILADDRESS,SENDFINALSTATUS,STATUS_ID) "
                    + "VALUES (" + id + ",'" + subject + "','" + body + "','" + email.getHtml() + "',to_date('" + date + "','DD/MM/YYYY HH24:MI:SS'),'" + email.getEmailId() + "'," + hasAttachment + ",'" + email.getSenderName() + "','" + email.getSenderEmailAddress() + "'," + sendFinalStatus + "," + email.getStatusId() + ")";


            //    System.out.println("inserting email statement:" + sqlString);

            stmt.executeUpdate(sqlString);

            stmt.close();

        } catch (Exception ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int getFilesCountPerPOS(Connection connection, String posCode, Date date) {

        int count = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();


        Statement stat;
        try {
            Format formatter;
            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedStartDate = formatter.format(startDate);

            String formattedEndDate = formatter.format(endDate);

            stat = connection.createStatement();
            String sql = "SELECT count(*) AS MAIL_COUNT FROM NEW_XYZ_BULK_EMAIL WHERE  CREATIONDATE  "
                    + "between to_date('" + formattedStartDate + "','DD/MM/YYYY HH24:MI:SS') AND to_date('" + formattedEndDate + "','DD/MM/YYYY HH24:MI:SS') "
                    + "AND POSCODE='" + posCode + "' AND (STATUS_ID=" + StatusManager.TO_BE_PROCESSED + " OR STATUS_ID=" + StatusManager.IN_PROGRESS + " OR STATUS_ID=" + StatusManager.COMPLETED + " OR STATUS_ID=" + StatusManager.FAILED + ")";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                count = res.getInt("MAIL_COUNT");

            }
            res.close();
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }


        return count;
    }

    public static void updateMailWithPos(Connection connection, Long emailId, String pos) {
        SANDLogger.getLogger().info("updateMailWithPos....Start");

        try {
            Statement st = connection.createStatement();

            String sqlString = "UPDATE NEW_XYZ_BULK_EMAIL SET POSCODE='" + pos + "' WHERE ID=" + emailId;
            System.out.print("Update Email with POS" + sqlString);
            st.executeUpdate(sqlString);
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        SANDLogger.getLogger().info("updateMailWithPos....ended");

    }

    public static void updateMailStatus(Connection connection, Email email) {
        //      SANDLogger.getLogger().info("updateMailStatus....Start");

        try {
            Statement stat = connection.createStatement();

            String sqlString = "UPDATE NEW_XYZ_BULK_EMAIL SET STATUS_ID=" + email.getStatusId() + " WHERE ID=" + email.getId();
            //   System.out.print(sqlString);
            stat.executeUpdate(sqlString);
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        //      SANDLogger.getLogger().info("updateMailStatus....ended");

    }

    public static void updateMailAfterSavingRecords(Connection connection, Email email) {
        //   SANDLogger.getLogger().info("updateMailAfterSavingRecords....Start");
        try {
            Statement stat = connection.createStatement();

            String sqlString = "UPDATE NEW_XYZ_BULK_EMAIL SET POSCODE='" + email.getPosCode() + "' ,ATTACHMENTURL='" + email.getAttachmentURL() + "',STATUS_ID=" + email.getStatusId() + " WHERE ID=" + email.getId();
            //   System.out.print(sqlString);
            stat.executeUpdate(sqlString);
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        //      SANDLogger.getLogger().info("updateMailAfterSavingRecords....ended");


    }

    public static void updateMailWithAttachmentName(Connection connection, Email email) {
        SANDLogger.getLogger().info("updateMailWithAttachmentName....Start");
        try {
            Statement stat = connection.createStatement();

            String sqlString = "UPDATE NEW_XYZ_BULK_EMAIL SET ATTACHMENTURL='" + email.getAttachmentURL() + "' WHERE ID=" + email.getId();
            //    System.out.print(sqlString);
            stat.executeUpdate(sqlString);
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        SANDLogger.getLogger().info("updateMailWithAttachmentName....ended");


    }

    public static void updateAfterReply(Connection connection, Email email) {
        //  SANDLogger.getLogger().info("updateAfterReply....Start");
        try {

            int sendFinalStatus = 0;

            if (email.getSendFinalStatus()) {
                sendFinalStatus = 1;
            }


            Statement stat = connection.createStatement();

            String sqlString = "UPDATE NEW_XYZ_BULK_EMAIL SET ATTACHMENTURL='" + email.getAttachmentURL() + "',STATUS_ID=" + email.getStatusId() + ",SENDFINALSTATUS=" + sendFinalStatus + " WHERE ID=" + email.getId();
            //      System.out.print(sqlString);
            stat.executeUpdate(sqlString);
            stat.close();

        } catch (SQLException ex) {
            Logger.getLogger(EmailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        //    SANDLogger.getLogger().info("updateAfterReply....ended");


    }
}
