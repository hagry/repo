/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.main;

import com.mobinil.xyz.dao.EmailDAO;
import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Attachment;
import com.mobinil.xyz.model.Email;
import com.mobinil.xyz.services.MailReaderService;
import com.mobinil.xyz.util.ConfigParameters;
import com.mobinil.xyz.util.DBUtil;
import com.moonrug.exchange.*;
import com.moonrug.exchange.search.Filter;
import com.moonrug.exchange.search.ValueFilter;
import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gado
 */
public class MailReader extends Thread {

    private IMessage imessage;
    private Session session;
    static int mailCount = 0;
    private Connection connection;

    private static void debug(String s) {
        SANDLogger.getLogger().info(s);
    }

    @Override
    public void run() {

        while (true) {

            try {
                long stime = System.currentTimeMillis();
                System.out.println("Before Main Function Mail Reader");
                connection = DBUtil.getConnection();
                mainFunction();
                System.out.println("After Main Function Mail Reader");
                //     long etime = System.currentTimeMillis();
                //      System.out.println("MailReader run took " + (etime - stime) / 1000);

            } catch (Exception e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Exception in mainFunction of the mail reader");
            }

            try {
                System.out.println("Before Sleep Mail Reader");
                sleep(ConfigParameters.SLEEP_TIME_VALUE);
                System.gc();
                DBUtil.closeConnection(connection);
                System.out.println("After Sleep Mail Reader");

            } catch (InterruptedException e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Mail Reader Thread can't sleep");

            }

            try {
                System.out.println("Before Clean Mail Reader");
                connection = DBUtil.getConnection();
                cleanup();
                System.out.println("After Clean Mail Reader");
            } catch (Exception e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Exception in cleanup of the mail reader");
            }

            try {
                System.out.println("MailReader Sleep Value = " + ConfigParameters.SLEEP_TIME_VALUE);
                sleep(ConfigParameters.SLEEP_TIME_VALUE);
                System.gc();
                DBUtil.closeConnection(connection);


            } catch (InterruptedException e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Mail Reader Thread can't sleep");

            }

        }

    }

    private void mainFunction() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Session.USERNAME, ConfigParameters.MAIL_USERNAME_VALUE);
        map.put(Session.PASSWORD, ConfigParameters.MAIL_PASSWORD_VALUE);
        map.put(Session.SERVER, "https://mail.mobinil.com");
        map.put(Session.DOMAIN, "mobinil");
        map.put(Session.PROTOCOL, Session.EWS_PROTOCOL);
        SessionManager sm = SessionManager.i();
        session = sm.create(map);
        //    System.out.println("mailReader. mainfunction create session");

        Property[] props = {IContentItem.MESSAGE_CLASS, IContentItem.BODY,
            IContentItem.HTML, IContentItem.SUBJECT,
            IContentItem.NORMALIZED_SUBJECT, IContentItem.HAS_ATTACH,
            IContentItem.HIDE_ATTACHMENTS,
            IContentItem.SENT_REPRESENTING_NAME,
            IContentItem.SENT_REPRESENTING_ADDRESS, IContentItem.PRIVATE,
            IContentItem.CREATION_TIME,
            IContentItem.LAST_MODIFICATION_TIME, IContentItem.CATEGORIES};
        try {

            IFolder inbox = session.getStore().getInbox();
            debug("MailReader : Connected into Main function Mail Reader");

            IContentItem[] contentItems;
            int mailSize = inbox.getContentCount();


            //      System.out.println("Fetch Size = " + ConfigParameters.FETCH_SIZE_VALUE);

//            if (ConfigParameters.FETCH_SIZE_VALUE + mailCount < mailSize) {
//                mailSize = ConfigParameters.FETCH_SIZE_VALUE + mailCount;
//            }


            contentItems = inbox.getItems(props);
            System.out.println("mail size = " + mailSize);
            //      System.out.println("mail count = " + mailCount);


            while (mailSize > 0) {


                for (IContentItem contentItem : contentItems) {
                    //  System.out.println("For loop inside mailReader mailCount=" + mailCount);
                    imessage = (IMessage) contentItem;
                    //  String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
                    String emailId = imessage.get(IMessage.ID).toString();
                    //debug("Check Mail Exist before or not into mail reader by mail id:" + emailId);

                    if (EmailDAO.findByEmailId(connection, emailId) == null) {


                        //         System.out.print("after finding email by ID MailReader ");
                        MailReaderService.saveMail(connection, imessage);

                        //    System.out.println("Before validAttachment Mail Reader");
                        //  long stime = System.currentTimeMillis();
                        Attachment attachment = MailReaderService.validAttachment(connection, imessage, session);
                        //      System.out.println("After validAttachment Mail Reader");
                        //  long etime = System.currentTimeMillis();
                        //     System.out.println("validAttachment run took " + (etime - stime) / 1000);

                        if (attachment != null) {
                            if (attachment.getRecordList() != null) {

                                MailReaderService.saveEmailRecords(connection, imessage, attachment);

                            }

                        }
//                        IFolder processed = session.getStore().getDrafts();
//                        imessage.moveTo(processed);

                    }

                }

                mailSize--;


            }
            //make mail count equal mail size to fetch new mails 
            //   mailCount = mailSize;
            session.close();
            sm.closeAll();

        } catch (ExchangeException e) {
            e.printStackTrace();
            SANDLogger.getLogger().error("Can't Access Mailbox");

        }
    }

    private void cleanup() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Session.USERNAME, ConfigParameters.MAIL_USERNAME_VALUE);
        map.put(Session.PASSWORD, ConfigParameters.MAIL_PASSWORD_VALUE);
        map.put(Session.SERVER, "https://mail.mobinil.com");
        map.put(Session.DOMAIN, "mobinil");
        map.put(Session.PROTOCOL, Session.EWS_PROTOCOL);
        SessionManager sm = SessionManager.i();
        session = sm.create(map);

        Property[] props = {IContentItem.MESSAGE_CLASS, IContentItem.BODY,
            IContentItem.HTML, IContentItem.SUBJECT,
            IContentItem.NORMALIZED_SUBJECT, IContentItem.HAS_ATTACH,
            IContentItem.HIDE_ATTACHMENTS,
            IContentItem.SENT_REPRESENTING_NAME,
            IContentItem.SENT_REPRESENTING_ADDRESS, IContentItem.PRIVATE,
            IContentItem.CREATION_TIME,
            IContentItem.LAST_MODIFICATION_TIME, IContentItem.CATEGORIES};
        try {
            IFolder inbox = session.getStore().getInbox();
            debug("Connected");
            IContentItem[] contentItems;
            // delete history
            contentItems = inbox.getItems(props);
            if (ConfigParameters.DELETE_HISTORY_VALUE) {
                Calendar previousMonthDay = Calendar.getInstance();
                previousMonthDay.add(Calendar.DAY_OF_YEAR, -1 * ConfigParameters.HISTORY_KEEP_DAYS_VALUE);
                Filter tooOldMailsFilter = new ValueFilter(
                        IMessage.CREATION_TIME,
                        ValueFilter.Operation.LESS_EQUAL,
                        previousMonthDay.getTime());
                inbox = session.getStore().getInbox();
                inbox.setFilter(tooOldMailsFilter);
                contentItems = inbox.getItems(props);
                Email emailtodelete;
                for (IContentItem contentItem : contentItems) {
                    imessage = (IMessage) contentItem;
                    //String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
                    String emailId = imessage.get(IMessage.ID).toString();
                    emailtodelete = EmailDAO.findByEmailId(connection, emailId);
                    if (emailtodelete != null && emailtodelete.getSendFinalStatus()) {
                        imessage.delete();
                        //setting mail count=0 to avoid miscounting mail numbers 
                        mailCount = 0;
                        debug("Message Deleted from Mailbox: " + emailtodelete.getId());
                    }
                }
                //    emailDAO.cleanUpEmails();

            }

            session.close();
            sm.closeAll();


        } catch (ExchangeException e) {
            e.printStackTrace();
            SANDLogger.getLogger().error("Can't Access Mailbox");

        }

    }
}
