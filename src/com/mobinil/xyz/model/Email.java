package com.mobinil.xyz.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Email implements Serializable {

    private Long id;
    private String emailId;
    private String subject;
    private String body;
    private String html;
    private String senderName;
    private String senderEmailAddress;
    private Date creationDate;
    private boolean hasAttachment;
    private String attachmentURL;
    private int statusId;
    private String posCode;
    private boolean sendFinalStatus;

    public Email() {
    }

    public Email(ResultSet rs) {
        try {
            this.id = rs.getLong("ID");
            this.emailId = rs.getString("EMAILID");
            this.subject = rs.getString("SUBJECT");
            this.body = rs.getString("BODY");
            this.html = rs.getString("HTML");
            this.senderName = rs.getString("SENDEREMAILADDRESS");
            this.senderEmailAddress = rs.getString("SENDEREMAILADDRESS");
            this.creationDate = rs.getDate("CREATIONDATE");
            this.hasAttachment = rs.getBoolean("HASATTACHMENT");
            this.attachmentURL = rs.getString("ATTACHMENTURL");
            this.statusId = rs.getInt("STATUS_ID");
            this.posCode = rs.getString("POSCODE");
            this.sendFinalStatus = rs.getBoolean("SENDFINALSTATUS");
        } catch (SQLException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public void setSenderEmailAddress(String senderEmailAddress) {
        this.senderEmailAddress = senderEmailAddress;
    }

    public boolean getSendFinalStatus() {
        return this.sendFinalStatus;
    }

    public void setSendFinalStatus(boolean sendFinalStatus) {
        this.sendFinalStatus = sendFinalStatus;
    }
}
