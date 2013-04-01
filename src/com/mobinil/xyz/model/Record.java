package com.mobinil.xyz.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Record implements Serializable {

    private Long id;
    private String dialNumber;
    private String simSerial;
    private String msg;
    private Date activationDate;
    private Long mailId;
    private int statusId;

    public Record() {
    }

    public Record(ResultSet rs) {
        try {
            this.id = rs.getLong("ID");
            this.dialNumber = rs.getString("DIALNUMBER");
            this.simSerial = rs.getString("SIMSERIAL");
            this.msg = rs.getString("MSG");
            this.activationDate = rs.getDate("ACTIVATIONDATE");
            this.mailId = rs.getLong("MAIL_ID");
            this.statusId = rs.getInt("STATUS_ID");

        } catch (SQLException ex) {
            Logger.getLogger(Record.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }

    public String getSimSerial() {
        return simSerial;
    }

    public void setSimSerial(String simSerial) {
        this.simSerial = simSerial;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Long getMailId() {
        return mailId;
    }

    public void setMailId(Long mailId) {
        this.mailId = mailId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
