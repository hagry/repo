package com.mobinil.xyz.main;

import java.io.File;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.main.MailReader;
import com.mobinil.xyz.main.ReplyChecker;
import com.mobinil.xyz.main.StatusChecker;
import com.mobinil.xyz.main.Validator;

public class XYZMain {

    public static void main(String[] args) {

        SANDLogger.getLogger().info("System Started");
        Validator validator = new Validator();
        validator.setName("Validator Thread");
        validator.start();

        File excelDir = new File("Excel_Files");
        if (!excelDir.exists()) {
            excelDir.mkdir();
        }


        System.out.println("Log: Starting Mail Reader");
        MailReader mailReader = new MailReader();
        mailReader.setName("Mail Reader and CleanUp Thread");
        mailReader.start();

        System.out.println("Log: Starting ReplyChecker");
        ReplyChecker replyChecker = new ReplyChecker();
        replyChecker.setName("Send Reply Thread");
        replyChecker.start();

        System.out.println("Log: Starting StatusChecker");
        StatusChecker statusChecker = new StatusChecker();
        statusChecker.setName("Status Checker Thread");
        statusChecker.start();


    }
}
