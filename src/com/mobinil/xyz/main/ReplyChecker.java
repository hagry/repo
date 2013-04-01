package com.mobinil.xyz.main;

import com.mobinil.xyz.dao.EmailDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mobinil.xyz.dao.EmailDAO;
import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.util.ConfigParameters;
import com.mobinil.xyz.util.DBUtil;
import com.mobinil.xyz.util.ErrorMessageSender;
import com.moonrug.exchange.*;
import java.sql.Connection;

public class ReplyChecker extends Thread {

    private IMessage imessage;
    private Session session;
    private Connection connection;

    private static void debug(String s) {
        SANDLogger.getLogger().info(s);
    }

    @Override
    public void run() {
        while (true) {

            try {
                //     long stime = System.currentTimeMillis();
                System.out.println("Run Reply Checker");
                connection = DBUtil.getConnection();
                sendReply();
                //    long etime = System.currentTimeMillis();
                //     System.out.println("ReplyChecker took " + (etime - stime) / 1000);

            } catch (Exception e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Exception in sendReply of the mail reader");
            }

            try {
                sleep(ConfigParameters.SLEEP_TIME_VALUE);
                System.gc();
                DBUtil.closeConnection(connection);


            } catch (InterruptedException e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Mail Reader Thread can't sleep");

            }

        }

    }

    private void sendReply() {
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

            // send completed mails
            System.out.println("Reply Checker: Find Completed Mails");
            List<String> completedMails = new ArrayList<String>(EmailDAO.findAllCompletedSuccessfullyMails(connection));
            debug("completedSuccessfullyMailsize: " + completedMails.size());

            IContentItem[] contentItems;
            contentItems =
                    inbox.getItems(props);

            for (IContentItem contentItem : contentItems) {
                imessage = (IMessage) contentItem;
                if (completedMails.contains(imessage.get(IMessage.ID).toString())) {
                    ErrorMessageSender.sendCompletedSuccessfullyMail(connection, imessage,
                            session.getStore().getSentItems());
                    completedMails.remove(imessage.get(IMessage.ID).toString());
                    if (completedMails.isEmpty()) {
                        System.out.println("Reply Checker:Completed Mails Empty");
                        break;
                    }
                }
            }


            if (!completedMails.isEmpty()) {
                System.out.println("Completed Mails Not Empty");
                for (String emailID : completedMails) {
                    ErrorMessageSender.createSendCompletedSuccessfullyMail(connection, emailID, inbox);
                    debug("PROCESSED Creating and Sending: " + emailID);
                }
            }
            //    debug("PROCESSED sending Successfully completedMails");


            // send failed mails
            System.out.println("Reply Checker: Find Failed Mails");
            completedMails = new ArrayList<String>(EmailDAO.findAllCompletedFailedMails(connection));
            debug("completedFailedMailsize: " + completedMails.size());
            contentItems = inbox.getItems(props);

            for (IContentItem contentItem : contentItems) {
                imessage =
                        (IMessage) contentItem;
                if (completedMails.contains(imessage.get(IMessage.ID).toString())) {
                    ErrorMessageSender.sendCompletedFailedMail(connection, imessage,
                            session.getStore().getSentItems());
                    debug("PROCESSED Sending:" + imessage.get(IMessage.ID).toString());
                    System.out.println("before remove");
                    completedMails.remove(imessage.get(IMessage.ID).toString());
                    if (completedMails.isEmpty()) {
                        System.out.println(
                                "Reply Checker:Failed Mails Empty");
                        break;
                    }
                }
            }

            if (!completedMails.isEmpty()) {
                System.out.println("Failed Mails Not Empty");
                for (String emailID : completedMails) {
                    ErrorMessageSender.createSendCompletedFailedMail(connection, emailID, inbox);
                    debug("PROCESSED Creating and Sending: " + emailID);
                }
            }
            debug("PROCESSED sending FailedcompletedMails");

            // send partially failed mails
            System.out.println("Reply Checker: Find Partial Failed Mails");
            completedMails = new ArrayList<String>(EmailDAO.findAllCompletedPartialFailedMails(connection));
            debug("completedPartiallyFailedMailsize: " + completedMails.size());
            contentItems = inbox.getItems(props);
            System.out.println("before loop of replychecker");

            for (IContentItem contentItem : contentItems) {
                imessage = (IMessage) contentItem;
                if (completedMails.contains(imessage.get(IMessage.ID).toString())) {
                    System.out.println("send partialFialed Message");
                    ErrorMessageSender.sendPartiallyFailedMail(connection, imessage,
                            session.getStore().getSentItems());
                    debug("PROCESSED Sending:" + imessage.get(IMessage.ID).toString());
                    completedMails.remove(imessage.get(IMessage.ID).toString());
                    if (completedMails.isEmpty()) {
                        System.out.println("Reply Checker:Partial Failed Mails Empty");
                        break;
                    }
                }
            }

            if (!completedMails.isEmpty()) {
                System.out.println("Partial Failed Mails Not Empty");
                for (String emailID : completedMails) {
                    System.out.println("createSendPartiallyFailedMail mailid" + emailID);
                    ErrorMessageSender.createSendPartiallyFailedMail(connection, emailID, inbox);
                    debug("PROCESSED Creating and Sending: " + emailID);
                }
            }
            debug("PROCESSED sending PartiallyFailedcompletedMails");

            session.close();
            sm.closeAll();

        } catch (ExchangeException e) {
            e.printStackTrace();
            SANDLogger.getLogger().error("Can't Access Mailbox");

        }
    }
}
