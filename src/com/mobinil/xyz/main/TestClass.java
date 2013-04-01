/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.main;

import com.mobinil.xyz.model.Email;
import com.mobinil.xyz.util.DBUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Brain
 */
public class TestClass {

    public static void main(String[] args) {
        try {

            Connection connection = DBUtil.getConnection();
            Statement stmt = connection.createStatement();

            Email email = new Email();

            email.setSubject("Subject");
            email.setBody("Body");
            email.setHtml("HTML");
            email.setCreationDate(new Date());
            email.setEmailId("emailId2");
            //email.setHasAttachment(Boolean.TRUE);
            email.setSenderName("Sender Name");
            email.setSenderEmailAddress("SenderEmailAddress");
            //    email.setSendFinalStatus(Boolean.FALSE);
            email.setStatusId(1);


       //     Long id = DBUtil.getNextVal(connection, stmt, "ID", "NEW_XYZ_BULK_EMAIL");

            Format formatter;
            String date = null;
            //  formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            date = formatter.format(email.getCreationDate());
//            String sqlString = "INSERT INTO NEW_XYZ_BULK_EMAIL (ID,SUBJECT,BODY,HTML,CREATIONDATE,EMAILID,HASATTACHMENT,SENDERNAME,SENDEREMAILADDRESS,SENDFINALSTATUS,STATUS_ID) "
//                    + "VALUES (" + id + ",'" + email.getSubject() + "','" + email.getBody() + "','" + email.getHtml() + "',to_date('" + date + "','DD/MM/YYYY HH24:MI:SS'),'" + email.getEmailId() + "'," + 1 + ",'" + email.getSenderName() + "','" + email.getSenderEmailAddress() + "'," + 0 + "," + email.getStatusId() + ")";
//            


//            String sqlString = "INSERT INTO NEW_XYZ_BULK_EMAIL (ID,SUBJECT,BODY,HTML,CREATIONDATE,EMAILID,HASATTACHMENT,SENDERNAME,SENDEREMAILADDRESS,SENDFINALSTATUS,STATUS_ID) "
//                    + "VALUES (" + id + ",'" + email.getSubject() + "','" + email.getBody() + "','" + email.getHtml() + "','" + date + "','" + email.getEmailId() + "'," + 1 + ",'" + email.getSenderName() + "','" + email.getSenderEmailAddress() + "'," + 0 + "," + email.getStatusId() + ")";
//
//            System.out.println("SQL:" + sqlString);
//            stmt.executeUpdate(sqlString);

            DBUtil.closeConnection(connection);

        } catch (SQLException ex) {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
