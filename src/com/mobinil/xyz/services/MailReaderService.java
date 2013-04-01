/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobinil.xyz.services;

import com.mobinil.xyz.dao.EmailDAO;
import com.mobinil.xyz.dao.PosDAO;
import com.mobinil.xyz.dao.RecordDAO;
import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.main.MailReader;
import com.mobinil.xyz.model.Attachment;
import com.mobinil.xyz.model.Email;
import com.mobinil.xyz.model.Record;
import com.mobinil.xyz.util.ConfigParameters;
import com.mobinil.xyz.util.ErrorMessageSender;
import com.mobinil.xyz.util.MailAttachmentFileErrorException;
import com.mobinil.xyz.util.StatusManager;
import com.moonrug.exchange.ExchangeException;
import com.moonrug.exchange.IAttachment;
import com.moonrug.exchange.IMessage;
import com.moonrug.exchange.Session;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Gado
 */
public class MailReaderService {

    public static Attachment validAttachment(Connection connection, IMessage imessage, Session session) {
        Attachment attachment = new Attachment();
        List<Record> recordList = new ArrayList<Record>();
        IAttachment[] attList = null;
        int validAttNo = 0, wrongExtNo = 0, wrongMimeNo = 0;
        try {
            if (imessage.getAttachments() != null && imessage.getAttachments().length != 0) {
                attList = imessage.getAttachments();
                for (IAttachment iAttachment : attList) {
                    if (isValidAttachmentExt(iAttachment.getName())) {
                        if (isValidAttachmentMimeType(iAttachment.getMimeTag())) {
                            validAttNo++;
                        } else {
                            wrongMimeNo++;
                        }
                    } else {
                        wrongExtNo++;
                    }
                }
                if (validAttNo > 1) {
                    ErrorMessageSender.sendInvalidAttNo(connection, imessage, session.getStore().getSentItems());
                    return null;
                }
                if (imessage.getAttachments().length == wrongMimeNo) {
                    ErrorMessageSender.sendAttachementMimeTypeError(connection,
                            imessage,
                            session.getStore().getSentItems());
                    return null;
                }
                if (imessage.getAttachments().length == wrongExtNo) {
                    ErrorMessageSender.sendAttachementNameExtError(connection,
                            imessage,
                            session.getStore().getSentItems());
                    return null;
                }
                for (IAttachment iAttachment : attList) {
                    if (isValidAttachmentExt(iAttachment.getName())) {
                        if (isValidAttachmentMimeType(iAttachment.getMimeTag())) {
//                            System.out.println("Valid attachement");
//                            System.out.println("Before isAttachmentFormatCorrect Mail Reader");
//                            long stime = System.currentTimeMillis();
                            attachment = isAttachmentFormatCorrect(connection, imessage, iAttachment, session);
//                            System.out.println("After isAttachmentFormatCorrect Mail Reader");
//                            long etime = System.currentTimeMillis();
//                            System.out.println("isAttachmentFormatCorrect run took " + (etime - stime) / 1000);
                            if (attachment.getRecordList() != null) {
                                recordList.addAll(attachment.getRecordList());
                            }

                        }
                    }
                }
            } else {
                ErrorMessageSender.sendEmailHasNoAtt(connection, imessage, session.getStore().getSentItems());
                return null;
            }
        } catch (ExchangeException e) {
            SANDLogger.getLogger().error(
                    "Can't read the attachment from email: " + imessage.get(IMessage.ID) + imessage.getSender().getAddress() + " - " + (Date) imessage.get(IMessage.CREATION_TIME));
            e.printStackTrace();
            System.out.println(e.getCause());
        }
        if (recordList.isEmpty()) {
            return null;
        } else {
            return attachment;
        }
    }

    private static boolean isValidAttachmentExt(String name) {
        for (String ext : ConfigParameters.EXCEL_EXTENSIONS_VALUE) {
            if (name.toLowerCase().contains(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidAttachmentMimeType(String attMime) {
        for (String mimeType : ConfigParameters.EXCEL_MIME_TYPES_VALUE) {
            if (attMime.toLowerCase().contains(mimeType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static Attachment isAttachmentFormatCorrect(Connection connection, IMessage imessage, IAttachment iAttachment, Session session) {
        File excelDir = new File("Excel_Files");
        //   String emailId = imessage.get(IMessage.ID).toString();
        //  Email email = EmailDAO.findByEmailId(connection, emailId);
        String pos = "";
        String attachmentName = excelDir + "/" + UUID.randomUUID() + iAttachment.getName().substring(
                iAttachment.getName().indexOf('.'));
        File file = new File(attachmentName);
        Workbook workbook = null;
        Sheet sh;
        FileInputStream fis = null;
        Attachment attachment = new Attachment();
        List<Record> records = new ArrayList<Record>();
        Record record;
        String dialNo = "", simSerialNo = "";
        int rowLenght = 0;
        boolean posExists = false;
        try {
            iAttachment.writeTo(new FileOutputStream(file));
            fis = new FileInputStream(file);
            try {
                workbook = WorkbookFactory.create(fis);
            } catch (Exception e) {
                ErrorMessageSender.sendCorruptedFileError(connection, imessage, session.getStore().getSentItems());
                return null;
            }
            sh = workbook.getSheetAt(0);
            rowLenght = sh.getLastRowNum();
            System.out.println("row_length:" + rowLenght);
            if (rowLenght > 2) {
                try {
                    pos = getCellStringValue(sh.getRow(0).getCell(1));
                    pos = pos.trim();
                    pos = pos.replaceAll(" ", "");
                    System.out.println("pos:" + pos);
                } catch (NullPointerException e) {
                    ErrorMessageSender.sendAttColNumberError(connection,
                            imessage,
                            session.getStore().getSentItems());
                    //       throw new MailAttachmentFileErrorException();

                }
                if (rowLenght <= ConfigParameters.MAX_RECORD_NUMBER_VALUE) {
                    if (pos != null && !pos.equals("") && pos.matches(ConfigParameters.POSCODE_REGX_VALUE)) {
                        System.out.println("Pos:Okay");
                        posExists = false;
                        System.out.println("searching for: " + pos);
                        posExists = PosDAO.isExist(connection, pos);
                        System.out.println("exists: " + posExists);
                        if (posExists || ConfigParameters.CHECK_POS_AVAILABILITY_VALUE) {
                            System.out.println("pos exists: yes");
                            if (EmailDAO.getFilesCountPerPOS(connection,
                                    pos, (Date) imessage.get(IMessage.CREATION_TIME)) < ConfigParameters.MAX_NUMBER_PER_POS_VALUE) {
                                System.out.println("parse attachement ");
                                for (int i = 2; i <= rowLenght; i++) {
//                                    debug("row: " + i);
                                    try {
                                        dialNo = getCellStringValue(sh.getRow(i).getCell(0));
                                        simSerialNo = getCellStringValue(sh.getRow(i).getCell(1));
                                        if (dialNo.isEmpty() && simSerialNo.isEmpty()) {
                                            continue;
                                        }
                                    } catch (NullPointerException e) {
                                        break;
                                    }
                                    record = new Record();
                                    record.setDialNumber(justifyDialNumer(dialNo));
                                    record.setSimSerial(simSerialNo);
                                    if (record.getDialNumber() == null || record.getDialNumber().trim().equals("") || record.getSimSerial() == null || record.getDialNumber().trim().equals("")) {
                                        //  debug("New Condition 1 By Shady (Dial or Sim null or empty)");
                                        //    debug("Wrong:dial: " + dialNo + " sim: " + simSerialNo);
                                        record.setStatusId(StatusManager.DIAL_SIM_FORMAT_ERROR);
                                    }
                                    if (containsSpaces(record.getDialNumber())
                                            == true || containsSpaces(record.getSimSerial()) == true) {
                                        //  debug("New Condition 2 By Shady (Dial or Sim contains spaces)");
                                        //   debug("Wrong:dial: " + dialNo + " sim: " + simSerialNo);
                                        record.setStatusId(StatusManager.DIAL_SIM_FORMAT_ERROR);
                                    }
                                    if (record.getDialNumber().length() > 255) {
                                        record.setDialNumber(record.getDialNumber().substring(0, 253));
                                    }
                                    if (record.getSimSerial().length() > 255) {
                                        record.setSimSerial(record.getSimSerial().substring(0, 253));
                                    }
                                    if (!isDialValid(dialNo) || !isSimValid(simSerialNo)) {
                                        //   debug("Wrong:dial: " + dialNo + " sim: " + simSerialNo);
                                        record.setStatusId(StatusManager.DIAL_SIM_FORMAT_ERROR);
                                    }


                                    records.add(record);
                                }

                                if (records.isEmpty()) {
                                    ErrorMessageSender.sendEmptyFileError(connection, imessage, session.getStore().getSentItems());
                                    //     throw new MailAttachmentFileErrorException();
                                }

                                records = RecordDAO.checkSims(connection, records);


                            } else {
                                saveEmailPOS(connection, imessage, pos);
                                ErrorMessageSender.sendExceededNoOfFilesPerPOSError(connection,
                                        imessage,
                                        session.getStore().getSentItems());

                                //     throw new MailAttachmentFileErrorException();
                            }
                        } else {
                            saveEmailPOS(connection, imessage, pos);
                            ErrorMessageSender.sendNotAvailablePOSError(connection,
                                    imessage,
                                    session.getStore().getSentItems());

                            //  throw new MailAttachmentFileErrorException();

                        }
                    } else {
                        saveEmailPOS(connection, imessage, pos);
                        ErrorMessageSender.sendInvalidPOSCodeError(connection,
                                imessage,
                                session.getStore().getSentItems());

                        //    throw new MailAttachmentFileErrorException();

                    }

                } else {
                    saveEmailPOS(connection, imessage, pos);
                    ErrorMessageSender.sendAttRowNumberError(connection, imessage, session.getStore().getSentItems());
                    //  throw new MailAttachmentFileErrorException();
                }
            } else {

                ErrorMessageSender.sendEmptyFileError(connection, imessage, session.getStore().getSentItems());
                // throw new MailAttachmentFileErrorException();
            }

            attachment.setAttachmentName(attachmentName);

            attachment.setPos(pos);

            attachment.setRecordList(records);
        } catch (MailAttachmentFileErrorException e) {
            try {
                if (fis != null) {
                    fis.close();

                }
            } catch (IOException ex) {
                Logger.getLogger(MailReader.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
            file.delete();
            return null;

        } catch (ExchangeException e) {
            SANDLogger.getLogger().error(
                    "Can't downlaod the attachment: " + iAttachment.getId() + " in email: " + imessage.getSubject() + " - " + imessage.getSender().getAddress() + " - " + (Date) imessage.get(IMessage.CREATION_TIME));
        } catch (FileNotFoundException e) {
            SANDLogger.getLogger().error(
                    "Can't find file named: " + attachmentName + " in email: " + imessage.getSubject() + " - " + imessage.getSender().getAddress() + " - " + (Date) imessage.get(IMessage.CREATION_TIME));
        } catch (IOException e) {
            try {
                ErrorMessageSender.sendCorruptedFileError(connection, imessage, session.getStore().getSentItems());
                return null;
            } catch (ExchangeException e1) {
                SANDLogger.getLogger().error(
                        "Can't downlaod the attachment: " + iAttachment.getId() + " in email: " + imessage.getSubject() + " - " + imessage.getSender().getAddress() + " - " + (Date) imessage.get(IMessage.CREATION_TIME));
            }

        } catch (Exception e) {
            e.printStackTrace();
            SANDLogger.getLogger().error(
                    "Can't parse the attachment: " + iAttachment.getId() + " in email: " + imessage.getSubject() + " - " + imessage.getSender().getAddress() + " - " + (Date) imessage.get(IMessage.CREATION_TIME));

        } finally {
            try {
                if (fis != null) {
                    fis.close();

                }
            } catch (IOException ex) {
                Logger.getLogger(MailReader.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

        }

        return attachment;
    }

    public static String getCellStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue() + "";
            case Cell.CELL_TYPE_BLANK:
//                SANDLogger.getLogger().error(
//                        "Cell: (" + (cell.getRowIndex() + 1) + " , " + cell.getColumnIndex() + ") Cell is empty");
                return "";
            case Cell.CELL_TYPE_FORMULA:
                SANDLogger.getLogger().error(
                        "Cell: (" + (cell.getRowIndex() + 1) + " , " + cell.getColumnIndex() + ") Cell is formula.");
                return "";
            case Cell.CELL_TYPE_BOOLEAN:
                SANDLogger.getLogger().error(
                        "Cell: (" + (cell.getRowIndex() + 1) + " , " + cell.getColumnIndex() + ") Cell isnot a number");
                return "";
            case Cell.CELL_TYPE_ERROR:
                SANDLogger.getLogger().error(
                        "Cell: (" + (cell.getRowIndex() + 1) + " , " + cell.getColumnIndex() + ") Cell invalid type");
                return "";
        }
        return "";
    }

    public static void saveMail(Connection connection, IMessage imessage) {
        System.out.println("begining of saveMail");
        Email email = new Email();
        email.setSubject((String) imessage.get(IMessage.SUBJECT));
        email.setBody((String) imessage.get(IMessage.BODY));
        email.setHtml((String) imessage.get(IMessage.HTML));
        email.setCreationDate((Date) imessage.get(IMessage.CREATION_TIME));
        email.setEmailId(imessage.get(IMessage.ID).toString());
        email.setHasAttachment((Boolean) imessage.get(IMessage.HAS_ATTACH));
        email.setSenderName(imessage.getSender().getName());
        email.setSenderEmailAddress(imessage.getSender().getAddress());
        email.setSendFinalStatus(false);
        email.setStatusId(StatusManager.TO_BE_PROCESSED);
        EmailDAO.saveMail(connection, email);
        System.out.println("end of saveMail");
    }

    public static void saveEmailRecords(Connection connection, IMessage imessage, Attachment attachment) {

        List<Record> records = attachment.getRecordList();
        System.out.println("savingEmailRecords");
        System.out.println("Records before save: " + records.size());
//        String emailId = Util.generateMailId(imessage.getSender().getAddress(), (Date) imessage.get(IMessage.CREATION_TIME), imessage.get(IMessage.SUBJECT).toString());
        String emailId = imessage.get(IMessage.ID).toString();
        Email email = EmailDAO.findByEmailId(connection, emailId);

        //  System.out.println("Attachment File Name:"+email.getAttachmentURL());

        HashMap<String, Record> recordsMap = new HashMap<String, Record>();
        try {

            for (int i = 0; i < records.size(); i++) {
                String newKey = records.get(i).getSimSerial();
                String dialNumber = records.get(i).getDialNumber();
                Record record = records.get(i);
                record.setMailId(email.getId());
                Record mapRecord = recordsMap.get(record.getSimSerial());
                if (mapRecord == null) {
                    RecordDAO.saveRecord(connection, record);
                    recordsMap.put(newKey, record);
                } else if (mapRecord != null && mapRecord.getDialNumber().equals(dialNumber)) {

                    record.setStatusId(StatusManager.SIM_SERIAL_ALREADY_EXISTS);
                    RecordDAO.saveRecord(connection, record);
                }
            }


            email.setPosCode(attachment.getPos());
            email.setStatusId(StatusManager.TO_BE_PROCESSED);
            email.setAttachmentURL(attachment.getAttachmentName());
            EmailDAO.updateMailAfterSavingRecords(connection, email);
            System.out.println(email.getAttachmentURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveEmailPOS(Connection connection, IMessage imessage, String pos) {
        System.out.println("saveEmailPOS");

        String emailId = imessage.get(IMessage.ID).toString();
        Email email = EmailDAO.findByEmailId(connection, emailId);
        email.setPosCode(pos);
        EmailDAO.updateMailWithPos(connection, email.getId(), pos);
    }

    public static String justifyDialNumer(String no) {

        if (no.startsWith("0")) {
            no = no.substring(1);
        }
        if (no.startsWith("12") && no.length() == 9) {
            no = "122" + no.substring(2);
        } else if (no.startsWith("17") && no.length() == 9) {
            no = "127" + no.substring(2);
        } else if (no.startsWith("18") && no.length() == 9) {
            no = "128" + no.substring(2);
        } else if (no.startsWith("150") && no.length() == 10) {
            no = "12" + no.substring(2);
        } else if (no.startsWith("10") && no.length() == 9) {
            no = "100" + no.substring(2);
        } else if (no.startsWith("16") && no.length() == 9) {
            no = "106" + no.substring(2);
        } else if (no.startsWith("19") && no.length() == 9) {
            no = "109" + no.substring(2);
        } else if (no.startsWith("151") && no.length() == 10) {
            no = "10" + no.substring(2);
        } else if (no.startsWith("11") && no.length() == 9) {
            no = "111" + no.substring(2);
        } else if (no.startsWith("14") && no.length() == 9) {
            no = "114" + no.substring(2);
        } else if (no.startsWith("152") && no.length() == 10) {
            no = "11" + no.substring(2);
        }

        return no;
    }

    public static boolean containsSpaces(String serial) {

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(serial);
        boolean found = matcher.find();
        return found;
    }

    public static boolean isDialValid(String str) throws IllegalArgumentException {

        if (str.matches(ConfigParameters.DIAL_REGX_VALUE)) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isSimValid(String str) throws IllegalArgumentException {

        if (str.matches(ConfigParameters.SIM_REGX_VALUE)) {
            return true;
        } else {
            return false;
        }

    }
}
