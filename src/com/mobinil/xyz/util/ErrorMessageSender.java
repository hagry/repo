package com.mobinil.xyz.util;

import com.mobinil.xyz.dao.EmailDAO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Email;
import com.moonrug.exchange.AttachmentSource;
import com.moonrug.exchange.EmailInfo;
import com.moonrug.exchange.ExchangeException;
import com.moonrug.exchange.IContentItem;
import com.moonrug.exchange.IFolder;
import com.moonrug.exchange.IMessage;
import com.moonrug.exchange.Recipient;
import com.moonrug.exchange.ResponseValues;
import com.moonrug.exchange.internal.Message;
import java.io.File;
import java.sql.Connection;
import java.util.Date;

public class ErrorMessageSender {

    private static EmailDAO emailDAO = new EmailDAO();

    public static void sendCompletedSuccessfullyMail(Connection connection, IMessage imessage,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_3);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    myReply = imessage.reply(folder, responseValues);
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();
                    email.setStatusId(StatusManager.COMPLETED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email successfull status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createSendCompletedSuccessfullyMail(Connection connection, String mailID,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            Email email = EmailDAO.findByEmailId(connection, mailID);
            IMessage myReply = (IMessage) folder.add();
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply.setSubject(email.getSubject());
                    myReply.setBody(ConfigParameters.STATUS_3);
                    myReply.addRecipient(new Recipient(Recipient.Type.TO,
                            new EmailInfo(email.getSenderName(), email.getSenderEmailAddress())));
                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();
                    email.setStatusId(StatusManager.COMPLETED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email successfull status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + email.getId());
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendCompletedFailedMail(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_4);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {

                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    myReply = imessage.reply(folder, responseValues);
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();

                    email.setStatusId(StatusManager.FAILED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email failed status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }

        }
    }

    public static void createSendCompletedFailedMail(Connection connection, String mailID,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            Email email = EmailDAO.findByEmailId(connection, mailID);
            IMessage myReply = (IMessage) folder.add();
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply.setSubject(email.getSubject());
                    myReply.setBody(ConfigParameters.STATUS_4);
                    myReply.addRecipient(new Recipient(Recipient.Type.TO,
                            new EmailInfo(email.getSenderName(), email.getSenderEmailAddress())));
                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();
                    email.setStatusId(StatusManager.FAILED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email successfull status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + email.getId());
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendPartiallyFailedMail(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_19);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    myReply = imessage.reply(folder, responseValues);
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();

                    email.setStatusId(StatusManager.PARTIALLY_FAILED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email partial status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }

        }
    }

    public static void createSendPartiallyFailedMail(Connection connection, String mailID,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            Email email = EmailDAO.findByEmailId(connection, mailID);
            IMessage myReply = (IMessage) folder.add();
            String attachmentName = "";
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply.setSubject(email.getSubject());
                    myReply.setBody(ConfigParameters.STATUS_19);
                    myReply.addRecipient(new Recipient(Recipient.Type.TO,
                            new EmailInfo(email.getSenderName(), email.getSenderEmailAddress())));
                    attachmentName = email.getAttachmentURL().replace("Excel_Files/", "");
                    FileInputStream fis = new FileInputStream(
                            email.getAttachmentURL());
                    myReply.addAttachment(new AttachmentSource(attachmentName, fis));
                    myReply.send();
                    email.setStatusId(StatusManager.PARTIALLY_FAILED);
                    email.setSendFinalStatus(true);
                    if (ConfigParameters.DELETE_EXCEL_HISTORY_VALUE) {
                        File file = new File(email.getAttachmentURL());
                        file.delete();
                        email.setAttachmentURL("Deleted");
                    }
                    EmailDAO.updateAfterReply(connection, email);
                    fis.close();
                    SANDLogger.getLogger().info("email successfull status sent");
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + email.getId());
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file -- attachment URL is null -- of email: "
                            + email.getId());
                } catch (FileNotFoundException e) {
                    email.setStatusId(StatusManager.TO_BE_PROCESSED);
                    email.setAttachmentURL(null);
                    EmailDAO.updateAfterReply(connection, email);
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
//                    e.printStackTrace();
                } catch (IOException e) {
                    SANDLogger.getLogger().error(
                            "can't send the attachment file of email: "
                            + email.getId());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendAttColNumberError(Connection connection,
            IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_5);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.INVALID_COLUMN_NO);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }

    }

    public static void sendAttRowNumberError(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_6);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = emailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.EXCEEDED_ALLOWED_REOCRD_NUMBER);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }
//        Email email = emailDAO.findByEmailId(imessage.get(IMessage.ID).toString());
//        emailDAO.saveOrUpdate(email);
    }

    public static void sendNotAvailablePOSError(Connection connection, IMessage imessage,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_7);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());

            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.NOT_AVAILABLE_POS_NUMBERS);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }




    }

    public static void sendExceededNoOfFilesPerPOSError(Connection connection, IMessage imessage,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_8);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.EXCEEDED_NO_OFF_FILES_PER_POS_ERROR);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);

                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }
//        Email email = emailDAO.findByEmailId(imessage.get(IMessage.ID).toString());
//        emailDAO.saveOrUpdate(email);
    }

//    public static void sendDialSIMFormatError(IMessage imessage, IFolder folder) {
//        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
//            ResponseValues responseValues = new ResponseValues();
//            responseValues.setNewBody(ConfigParameters.STATUS_9);
//            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
//                imessage.getSender())};
//            responseValues.setRecipients(recipients);
//            IMessage myReply;
////            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
//            String emailId = imessage.get(IMessage.ID).toString();
//            Email email = EmailDAO.findByEmailId(emailId);
//            if (email != null && !email.getSendFinalStatus()) {
//                try {
//                    myReply = imessage.reply(folder, responseValues);
//                    myReply.send();
//                    email.setStatus(StatusManager.getDialSIMFormatError());
//                    email.setSendFinalStatus(true);
//                    emailDAO.saveOrUpdate(email);
//                } catch (ExchangeException e) {
//                    SANDLogger.getLogger().error(
//                            "Can't access mailbox to send mail:"
//                            + imessage.get(IMessage.ID));
//                    e.printStackTrace();
//                }
//            }
//        }
////        Email email = emailDAO.findByEmailId(imessage.get(IMessage.ID).toString());
////        emailDAO.saveOrUpdate(email);
//    }
    public static void sendAttachementNameExtError(Connection connection, IMessage imessage,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_10);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.ATTACHEMENT_NAME_EXST_ERROR);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendAttachementMimeTypeError(Connection connection, IMessage imessage,
            IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_11);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
//            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.ATTACHEMENT_MIME_TYPE_ERROR);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }
//        Email email = emailDAO.findByEmailId(imessage.get(IMessage.ID).toString());
//        emailDAO.saveOrUpdate(email);
    }

//    public static void sendSIMAlreadyExistError(IMessage imessage,
//            IFolder folder) {
//        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
//            ResponseValues responseValues = new ResponseValues();
//            responseValues.setNewBody(ConfigParameters.STATUS_12);
//            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
//                imessage.getSender())};
//            responseValues.setRecipients(recipients);
//            IMessage myReply;
////            String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
//            String emailId = imessage.get(IMessage.ID).toString();
//            Email email = E.findByEmailId(emailId);
//            if (email != null && !email.getSendFinalStatus()) {
//                try {
//                    myReply = imessage.reply(folder, responseValues);
//                    myReply.send();
//                    email.setStatus(StatusManager.getSimAlreadyExistError());
//                    email.setSendFinalStatus(true);
//                    emailDAO.saveOrUpdate(email);
//                } catch (ExchangeException e) {
//                    SANDLogger.getLogger().error(
//                            "Can't access mailbox to send mail:"
//                            + imessage.get(IMessage.ID));
//                    e.printStackTrace();
//                }
//            }
//        }
////        Email email = emailDAO.findByEmailId(imessage.get(IMessage.ID).toString());
////        emailDAO.saveOrUpdate(email);
//    }
    public static void sendEmptyFileError(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_13);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection,
                    emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.EMPTY_FILE);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }

    }

    public static void sendCorruptedFileError(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_14);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.CORRUPT_FILE);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }

    }

    public static void sendInvalidPOSCodeError(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_15);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.ATT_FILE_HAS_DUPLICATES);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }

    }

    public static void sendInvalidAttNo(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_16);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.INVALID_ATT_NO);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();
                }
            }
        }

    }

    public static void sendEmailHasNoAtt(Connection connection, IMessage imessage, IFolder folder) {
        if (ConfigParameters.SEND_MAIL_REPLAY_VALUE) {
            ResponseValues responseValues = new ResponseValues();
            responseValues.setNewBody(ConfigParameters.STATUS_17);
            Recipient[] recipients = new Recipient[]{new Recipient(Recipient.Type.TO,
                imessage.getSender())};
            responseValues.setRecipients(recipients);
            IMessage myReply;
            String emailId = imessage.get(IMessage.ID).toString();
            Email email = EmailDAO.findByEmailId(connection, emailId);
            if (email != null && !email.getSendFinalStatus()) {
                try {
                    myReply = imessage.reply(folder, responseValues);
                    myReply.send();
                    email.setStatusId(StatusManager.NO_ATTACHMENT);
                    email.setSendFinalStatus(true);
                    EmailDAO.updateAfterReply(connection, email);
                } catch (ExchangeException e) {
                    SANDLogger.getLogger().error(
                            "Can't access mailbox to send mail:"
                            + imessage.get(IMessage.ID));
                    e.printStackTrace();

                }
            }
        }

    }
}
