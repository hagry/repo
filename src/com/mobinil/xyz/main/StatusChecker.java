package com.mobinil.xyz.main;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.mobinil.xyz.dao.EmailDAO;
import com.mobinil.xyz.dao.RecordDAO;
import com.mobinil.xyz.dao.StatusDAO;
import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.model.Email;
import com.mobinil.xyz.model.Record;
import com.mobinil.xyz.model.Status;
import com.mobinil.xyz.util.ConfigParameters;
import com.mobinil.xyz.util.DBUtil;
import com.mobinil.xyz.util.StatusManager;
import java.sql.Connection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class StatusChecker extends Thread {

    private EmailDAO emailDAO = new EmailDAO();
    private RecordDAO recordDAO = new RecordDAO();
    private Connection connection;

    private static void debug(String s) {
        SANDLogger.getLogger().info(s);

    }

    @Override
    public void run() {

        while (true) {
            try {
                //    long stime = System.currentTimeMillis();
                System.out.println("Log: Loop Start StatusChecker");
                connection = DBUtil.getConnection();
                mainFunction();
//                long etime = System.currentTimeMillis();
//                System.out.println("StatusChecker took = " + (etime - stime) / 1000);
            } catch (Exception e) {
                e.printStackTrace();
                SANDLogger.getLogger().error("Status Checker Thread main function exception");
            }
            try {
                sleep(ConfigParameters.SLEEP_TIME_VALUE);
                System.gc();
                DBUtil.closeConnection(connection);
            } catch (InterruptedException e) {
                SANDLogger.getLogger().error("Status Checker Thread can't sleep");
                e.printStackTrace();
            }
            System.out.println("Log: Loop End  StatusChecker");
        }

    }

    private void mainFunction() {
        SANDLogger.getLogger().info("StatusChecker Started System Checker");

        List<Email> emails =
                EmailDAO.findAllToBeProcessed(connection);
        //  System.out.println("StatusChecker Got All emails ");

        for (Email email : emails) {
            //  SANDLogger.getLogger().info("StatusChecker processing email " + email.getSenderEmailAddress());

            List<Record> records = RecordDAO.getRecordsByEmailId(connection, email.getId());

            if (records.isEmpty()) {
                SANDLogger.getLogger().info("StatusChecker updating the email with empty file and not sending");
                email.setStatusId(StatusManager.EMPTY_FILE);
                EmailDAO.updateMailStatus(connection, email);
                continue;
            }

//            System.out.println("StatusChecker calling getEmailRecordsStatus =");
            int emailStatus = getEmailRecordsStatus(email, records);
//            System.out.println("StatusChecker get email records status = " + emailStatus);
            //         SANDLogger.getLogger().info("email status " + emailStatus);
            if (emailStatus == 1) {
                System.out.println("StatusChecker calling prepareAttachmentFile ");
                prepareAttachmentFile(email, records);
                switch (isFailedEmail(email, records)) {
                    case 3:
                        email.setStatusId(StatusManager.COMPLETED);
                        EmailDAO.updateMailStatus(connection, email);
                        System.out.println("Set Mail Status Completed");
                        SANDLogger.getLogger().info("email send");
                        break;
                    case 4:
                        email.setStatusId(StatusManager.FAILED);
                        EmailDAO.updateMailStatus(connection, email);
                        System.out.println("Set Mail Status Failed");
                        SANDLogger.getLogger().info("email send");
                        break;
                    case 19:
                        email.setStatusId(StatusManager.PARTIALLY_FAILED);
                        EmailDAO.updateMailStatus(connection, email);
                        System.out.println("Set Mail Status Partially Failed");

                        SANDLogger.getLogger().info("email send");
                        break;
                    default:
                        SANDLogger.getLogger().info("we reached the default which we shouldn't reach");
                        SANDLogger.getLogger().info("value = " + isFailedEmail(email, records));
                        System.out.println("StatusChecker we shouldn't reach here mainFunction");
                        break;
                }

            } else if (emailStatus == 0) {
                email.setStatusId(StatusManager.TO_BE_PROCESSED);
                EmailDAO.updateMailStatus(connection, email);
                //   System.out.println("Set Mail Status To Be Processed");
                //    SANDLogger.getLogger().info("email status is 0 and no email send ");

            }
        }

    }

    private void prepareAttachmentFile(Email email, List<Record> records) {

        debug("beginig of prepareAttachmentFile for email: " + email.getId());
        //long starttime = System.currentTimeMillis();
        String attachmentFile;
        try {
            attachmentFile = email.getAttachmentURL();
            if (attachmentFile == null) {
                attachmentFile = "Excel_Files/" + UUID.randomUUID() + ".xls";
            }
//            System.out.println("path 1 ");
        } catch (NullPointerException e) {
            attachmentFile = "Excel_Files/" + UUID.randomUUID() + ".xls";
//            System.out.println("path 2 ");
        }

        try {

            if (attachmentFile.endsWith("x") || attachmentFile.endsWith("X")) {
                attachmentFile = attachmentFile.substring(0, attachmentFile.length() - 1);
            }
            Workbook workbook = new HSSFWorkbook();

            Sheet sh;
            sh = workbook.createSheet();
            int count = 0;
            sh.createRow(count);
            try {
                sh.getRow(count).getCell(0).setCellValue("POSCode");
            } catch (NullPointerException e) {
                sh.getRow(count).createCell(0);
                sh.getRow(count).getCell(0).setCellValue("POSCode");
            }
            try {
                sh.getRow(count).getCell(1).setCellValue(email.getPosCode());
            } catch (NullPointerException e) {
                sh.getRow(count).createCell(1);
                sh.getRow(count).getCell(1).setCellValue(email.getPosCode());
            }
            sh.createRow(++count);
            try {
                sh.getRow(count).getCell(0).setCellValue("Dial Number");
            } catch (NullPointerException e) {
                sh.getRow(count).createCell(0);
                sh.getRow(count).getCell(0).setCellValue("Dial Number");
            }

            try {
                sh.getRow(count).getCell(1).setCellValue("SIM Serial");
            } catch (NullPointerException e) {
                sh.getRow(count).createCell(1);
                sh.getRow(count).getCell(1).setCellValue("SIM Serial");
            }

            try {
                sh.getRow(count).getCell(2).setCellValue("حالة الرقم");
            } catch (NullPointerException e) {
                sh.getRow(count).createCell(2);
                sh.getRow(count).getCell(2).setCellValue("حالة الرقم");
            }
            //         System.out.println("going to check for already exist with samePOS ");
//            long stime = System.currentTimeMillis();
            List<Boolean> results = recordDAO.alreadyExistsWithSamePOS(connection, records, email.getPosCode());
//            long etime = System.currentTimeMillis();
//            System.out.println("alreadyExistsWithSamePOS took" + (etime - stime) / 1000);

//            debug("before loop count ");
            Set<Record> recordSet = new HashSet<Record>(records);
            for (Record record : recordSet) {

                Status recoStatus = StatusDAO.findStatusById(connection, record.getStatusId());
                count++;
                sh.createRow(count);
                try {
                    sh.getRow(count).getCell(0).setCellValue(
                            record.getDialNumber());
                } catch (NullPointerException e) {
                    sh.getRow(count).createCell(0);
                    sh.getRow(count).getCell(0).setCellValue(
                            record.getDialNumber());
                }
                try {
                    sh.getRow(count).getCell(1).setCellValue(
                            record.getSimSerial());
                } catch (NullPointerException e) {
                    sh.getRow(count).createCell(1);
                    sh.getRow(count).getCell(1).setCellValue(
                            record.getSimSerial());
                }

                if (record.getStatusId() == StatusManager.SIM_SERIAL_ALREADY_EXISTS && results.size() != 0) {
                    if (results.get(count - 2)) {
                        try {
                            sh.getRow(count).getCell(2).setCellValue(
                                    recoStatus.getArabicDescription() + " تم أرساله بواسطتك");
                        } catch (NullPointerException e) {
                            sh.getRow(count).createCell(2);
                            sh.getRow(count).getCell(2).setCellValue(
                                    recoStatus.getArabicDescription() + " تم أرساله بواسطتك");
                        }
                    } else {
                        try {
                            sh.getRow(count).getCell(2).setCellValue(
                                    recoStatus.getArabicDescription() + " تم أرساله بواسطة موزع أخر");
                        } catch (NullPointerException e) {
                            sh.getRow(count).createCell(2);
                            sh.getRow(count).getCell(2).setCellValue(
                                    recoStatus.getArabicDescription() + " تم أرساله بواسطة موزع أخر");
                        }
                    }

                } else {
                    try {
                        sh.getRow(count).getCell(2).setCellValue(
                                recoStatus.getArabicDescription());
                    } catch (NullPointerException e) {
                        sh.getRow(count).createCell(2);
                        sh.getRow(count).getCell(2).setCellValue(
                                recoStatus.getArabicDescription());
                    }
                }

            }

            debug("before creating file");
            File file = new File(attachmentFile);
            if (file.isFile()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            email.setAttachmentURL(attachmentFile);
            System.out.println("before saveorupdate in the status  Chekcer");
            EmailDAO.updateMailWithAttachmentName(connection, email);
            debug(file.getAbsolutePath());
            debug("end of prepareAttachment file");
            debug("After creating file");
            //       System.out.println("StatusChecker prepareAttachmentFile time took for prepareAttachment = " + (System.currentTimeMillis() - starttime) / 1000);
        } catch (Exception e) {
            SANDLogger.getLogger().error(
                    "can't delete and create new for the attachment file of email: " + email.getId() + " subject: " + email.getSubject() + " address: " + email.getSenderEmailAddress() + " attachmentfile:  " + email.getAttachmentURL());
            e.printStackTrace();
        }

    }

    private int isFailedEmail(Email email, List<Record> records) {

        System.out.println("Is Failed Email Status Checker " + email.getId());
        int successNo = 0, failedNo = 0;
        for (Record record : records) {
            if (record.getStatusId() == StatusManager.COMPLETED) {
                successNo++;
            } else {
                failedNo++;
            }

        }
        System.out.println("success = " + successNo + "  failed:" + failedNo + " total: " + records.size());
        if (successNo == records.size()) {
            return 3;
        } else if (failedNo == records.size()) {
            return 4;
        } else {
            return 19;
        }
    }

    private int getEmailRecordsStatus(Email email, List<Record> records) {

        //   System.out.println("StatusChecker  Email Record Status Status Checker " + email.getId());
        for (Record record : records) {
            if (record.getStatusId() == StatusManager.TO_BE_PROCESSED) {
                return 0;
            } else if (record.getStatusId() == StatusManager.IN_PROGRESS) {
                return -1;
            }

        }
        return 1;
    }
}
